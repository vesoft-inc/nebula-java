/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.THeaderProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.THeaderTransport;
import com.facebook.thrift.transport.THttp2Client;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.facebook.thrift.utils.StandardCharsets;
import com.google.common.base.Charsets;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import com.vesoft.nebula.graph.VerifyClientVersionReq;
import com.vesoft.nebula.graph.VerifyClientVersionResp;
import com.vesoft.nebula.util.SslUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SyncConnection extends Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConnection.class);

    protected TTransport transport = null;
    protected TProtocol protocol = null;
    private GraphService.Client client = null;
    private int timeout = 0;
    private SSLParam sslParam = null;
    private boolean enabledSsl = false;
    private SSLSocketFactory sslSocketFactory = null;
    private boolean useHttp2 = false;

    @Override
    public void open(HostAddress address, int timeout, SSLParam sslParam)
            throws IOErrorException, ClientServerIncompatibleException {
        this.open(address, timeout, sslParam, false);
    }

    @Override
    public void open(HostAddress address, int timeout, SSLParam sslParam, boolean isUseHttp2)
            throws IOErrorException, ClientServerIncompatibleException {
        try {
            this.serverAddr = address;
            this.timeout = timeout <= 0 ? Integer.MAX_VALUE : timeout;
            this.enabledSsl = true;
            this.sslParam = sslParam;
            this.useHttp2 = isUseHttp2;
            if (sslSocketFactory == null) {
                if (sslParam.getSignMode() == SSLParam.SignMode.CA_SIGNED) {
                    sslSocketFactory =
                            SslUtil.getSSLSocketFactoryWithCA((CASignedSSLParam) sslParam);
                } else {
                    sslSocketFactory =
                            SslUtil.getSSLSocketFactoryWithoutCA((SelfSignedSSLParam) sslParam);
                }
            }
            if (isUseHttp2) {
                getProtocolWithTlsHttp2();
            } else {
                getProtocolForTls();
            }

            client = new GraphService.Client(protocol);

            // check if client version matches server version
            VerifyClientVersionResp resp =
                    client.verifyClientVersion(new VerifyClientVersionReq());
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                client.getInputProtocol().getTransport().close();
                throw new ClientServerIncompatibleException(new String(resp.getError_msg(),
                        Charsets.UTF_8));
            }
        } catch (TException | IOException e) {
            close();
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    @Override
    public void open(HostAddress address, int timeout) throws IOErrorException,
            ClientServerIncompatibleException {
        this.open(address, timeout, false);
    }

    @Override
    public void open(HostAddress address, int timeout, boolean isUseHttp2)
            throws IOErrorException, ClientServerIncompatibleException {
        try {
            this.serverAddr = address;
            this.timeout = timeout <= 0 ? Integer.MAX_VALUE : timeout;
            if (isUseHttp2) {
                getProtocolForHttp2();
            } else {
                getProtocol();
            }
            client = new GraphService.Client(protocol);

            // check if client version matches server version
            VerifyClientVersionResp resp =
                    client.verifyClientVersion(new VerifyClientVersionReq());
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                client.getInputProtocol().getTransport().close();
                throw new ClientServerIncompatibleException(new String(resp.getError_msg(),
                        Charsets.UTF_8));
            }
        } catch (TException e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    /**
     * create protocol for http2 with tls
     */
    private void getProtocolWithTlsHttp2() {
        String url = "https://" + serverAddr.getHost() + ":" + serverAddr.getPort();
        TrustManager trustManager;
        if (SslUtil.getTrustManagers() == null || SslUtil.getTrustManagers().length == 0) {
            trustManager = null;
        } else {
            trustManager = SslUtil.getTrustManagers()[0];
        }
        this.transport = new THttp2Client(url, sslSocketFactory, trustManager)
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout);
        transport.open();
        this.protocol = new TBinaryProtocol(transport);
    }

    /**
     * create protocol for http2 without tls
     */
    private void getProtocolForTls() throws IOException {
        this.transport = new THeaderTransport(new TSocket(
                sslSocketFactory.createSocket(serverAddr.getHost(),
                        serverAddr.getPort()), this.timeout, this.timeout));
        this.protocol = new THeaderProtocol((THeaderTransport) transport);
    }

    /**
     * create protocol for http2
     */
    private void getProtocolForHttp2() {
        String url = "http://" + serverAddr.getHost() + ":" + serverAddr.getPort();
        this.transport = new THttp2Client(url)
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout);
        transport.open();
        this.protocol = new TBinaryProtocol(transport);
    }

    /**
     * create protocol for tcp
     */
    private void getProtocol() {
        this.transport = new THeaderTransport(new TSocket(
                serverAddr.getHost(), serverAddr.getPort(), this.timeout, this.timeout));
        transport.open();
        this.protocol = new THeaderProtocol((THeaderTransport) transport);
    }


    /*
     * Because the code generated by Fbthrift does not handle the seqID,
     * the message will be dislocation when the timeout occurs,
     * resulting in unexpected response,
     * so when the timeout occurs,
     * the connection will be reopened to avoid the impact of the message.
     * So when timeout happend need to use reopen
     *
     * @throws IOErrorException if io problem happen
     */
    @Override
    public void reopen() throws IOErrorException, ClientServerIncompatibleException {
        close();
        if (enabledSsl) {
            open(serverAddr, timeout, sslParam, useHttp2);
        } else {
            open(serverAddr, timeout, useHttp2);
        }
    }

    public AuthResult authenticate(String user, String password)
            throws AuthFailedException, IOErrorException, ClientServerIncompatibleException {
        try {
            AuthResponse resp = client.authenticate(user.getBytes(), password.getBytes());
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                if (resp.error_msg != null) {
                    throw new AuthFailedException(new String(resp.error_msg));
                } else {
                    throw new AuthFailedException(
                            "The error_msg is null, "
                                    + "maybe the service not set or the response is disorder.");
                }
            }
            return new AuthResult(resp.getSession_id(), resp.getTime_zone_offset_seconds());
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                } else if (te.getType() == TTransportException.TIMED_OUT
                        || te.getMessage().contains("Read timed out")) {
                    reopen();
                    throw new IOErrorException(IOErrorException.E_TIME_OUT, te.getMessage());
                } else if (te.getType() == TTransportException.NOT_OPEN) {
                    throw new IOErrorException(IOErrorException.E_NO_OPEN, te.getMessage());
                }
            }
            throw new AuthFailedException(String.format("Authenticate failed: %s", e.getMessage()));
        }
    }

    public ExecutionResponse execute(long sessionID, String stmt)
            throws IOErrorException {
        return executeWithParameter(sessionID,
                stmt, (Map<byte[], com.vesoft.nebula.Value>) Collections.EMPTY_MAP);
    }

    public ExecutionResponse executeWithParameter(long sessionID, String stmt,
                                                  Map<byte[], com.vesoft.nebula.Value> parameterMap)
            throws IOErrorException {
        try {
            return client.executeWithParameter(sessionID, stmt.getBytes(), parameterMap);
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                } else if (te.getType() == TTransportException.NOT_OPEN) {
                    throw new IOErrorException(IOErrorException.E_NO_OPEN, te.getMessage());
                } else if (te.getType() == TTransportException.TIMED_OUT
                        || te.getMessage().contains("Read timed out")) {
                    try {
                        reopen();
                    } catch (ClientServerIncompatibleException ex) {
                        LOGGER.error(ex.getMessage());
                    }
                    throw new IOErrorException(IOErrorException.E_TIME_OUT, te.getMessage());
                }
            }
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public String executeJson(long sessionID, String stmt)
            throws IOErrorException {
        return executeJsonWithParameter(sessionID, stmt,
                (Map<byte[], com.vesoft.nebula.Value>) Collections.EMPTY_MAP);
    }

    public String executeJsonWithParameter(long sessionID, String stmt,
                                           Map<byte[], com.vesoft.nebula.Value> parameterMap)
            throws IOErrorException {
        try {
            byte[] result =
                    client.executeJsonWithParameter(sessionID, stmt.getBytes(), parameterMap);
            return new String(result, StandardCharsets.UTF_8);
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                } else if (te.getType() == TTransportException.NOT_OPEN) {
                    throw new IOErrorException(IOErrorException.E_NO_OPEN, te.getMessage());
                } else if (te.getType() == TTransportException.TIMED_OUT
                        || te.getMessage().contains("Read timed out")) {
                    try {
                        reopen();
                    } catch (ClientServerIncompatibleException ex) {
                        LOGGER.error(ex.getMessage());
                    }
                    throw new IOErrorException(IOErrorException.E_TIME_OUT, te.getMessage());
                }
            }
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public void signout(long sessionId) {
        client.signout(sessionId);
    }

    @Override
    public boolean ping() {
        try {
            execute(0, "YIELD 1;");
            return true;
        } catch (IOErrorException e) {
            return false;
        }
    }

    @Override
    public boolean ping(long sessionID) {
        try {
            execute(sessionID, "YIELD 1;");
            return true;
        } catch (IOErrorException e) {
            return false;
        }
    }


    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
            transport = null;
        }
    }

}
