/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import static com.vesoft.nebula.driver.graph.exception.IOErrorException.E_TIME_OUT;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.utils.ClientVersion;
import com.vesoft.nebula.driver.graph.utils.TlsUtil;
import com.vesoft.nebula.proto.common.ClientInfo;
import com.vesoft.nebula.proto.common.Common;
import com.vesoft.nebula.proto.graph.AuthRequest;
import com.vesoft.nebula.proto.graph.AuthResponse;
import com.vesoft.nebula.proto.graph.ExecuteRequest;
import com.vesoft.nebula.proto.graph.ExecuteResponse;
import com.vesoft.nebula.proto.graph.GraphServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcConnection extends Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcConnection.class);

    private ManagedChannel                            channel;
    private GraphServiceGrpc.GraphServiceBlockingStub stub;
    private long                                      connectTimeout = 0;
    private long                                      requestTimeout = 0;

    private final Charset charset = Charsets.UTF_8;

    @Override
    public void open(HostAddress address,
                     NebulaClient.Builder builder) throws IOErrorException {
        this.serverAddr = address;
        this.connectTimeout = builder.connectTimeoutMills;
        this.requestTimeout = builder.requestTimeoutMills;
        String formattedHost = address.getHost();
        if (formattedHost.contains(":") && !formattedHost.startsWith("[")) {
            formattedHost = "[" + formattedHost + "]";
        }
        if (builder.enableTls) {
            try {
                NettyChannelBuilder channelBuilder = NettyChannelBuilder
                        .forAddress(formattedHost, address.getPort())
                        .useTransportSecurity()
                        .sslContext(TlsUtil.getSslContext(builder.disableVerifyServerCert,
                                                          builder.tlsCa,
                                                          builder.tlsCert,
                                                          builder.tlsKey))
                        .maxInboundMessageSize(Integer.MAX_VALUE);
                channel = channelBuilder.build();
            } catch (SSLException e) {
                throw new IOErrorException(IOErrorException.E_SSL_ERROR, e.getMessage());
            }
        } else {
            channel = NettyChannelBuilder
                    .forAddress(formattedHost, address.getPort())
                    .usePlaintext()
                    .maxInboundMessageSize(Integer.MAX_VALUE)
                    .build();
        }
        stub = GraphServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void close() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
        stub = null;
    }

    @Override
    public boolean ping(long sessionID, long timeoutMs) throws IOErrorException {
        ExecuteResponse response = execute(sessionID, "RETURN 1", timeoutMs);
        return ErrorCode.SUCCESSFUL_COMPLETION.code
                .equals(response.getStatus().getCode().toString(charset));
    }

    public AuthResult authenticate(String user, Map<String, Object> authOptions)
            throws AuthFailedException, IOErrorException {
        try {
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .setLang(ClientInfo.Language.JAVA)
                    .setProtocolVersion(Common
                                                .getDescriptor()
                                                .getOptions()
                                                .getExtension(Common.protocolVersion))
                    .setVersion(ByteString.copyFrom(ClientVersion.clientVersion, charset))
                    .build();
            ByteString userString = user == null ? ByteString.copyFrom("", charset)
                    : ByteString.copyFrom(user, charset);
            String authInfoString = JSON.toJSONString(authOptions);
            AuthRequest authReq = AuthRequest.newBuilder()
                    .setUsername(userString)
                    .setAuthInfo(ByteString.copyFrom(authInfoString, charset))
                    .setClientInfo(clientInfo)
                    .build();

            AuthResponse resp = stub
                    .withDeadlineAfter(connectTimeout, TimeUnit.MILLISECONDS)
                    .authenticate(authReq);
            String code = resp.getStatus().getCode().toString(charset);
            if (!ErrorCode.SUCCESSFUL_COMPLETION.code.equals(code)) {
                close();
                throw new AuthFailedException(resp.getStatus().getMessage().toString(charset));
            }
            return new AuthResult(resp.getSessionId(), resp.getVersion().toString(charset));
        } catch (Exception e) {
            close();
            if (e instanceof StatusRuntimeException
                    && (((StatusRuntimeException) e)
                    .getStatus().getCode() == Code.DEADLINE_EXCEEDED)) {
                throw new AuthFailedException(String.format("authenticate to %s timeout after %dms",
                                                            serverAddr.toString(),
                                                            connectTimeout));
            }
            if (e instanceof StatusRuntimeException) {
                throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
            }
            throw e;
        }
    }

    public ExecuteResponse execute(long sessionID, String stmt, long timeout)
            throws IOErrorException {
        if (stmt == null) {
            throw new NullPointerException("statement is null.");
        }
        try {
            ExecuteRequest request = ExecuteRequest.newBuilder()
                    .setSessionId(sessionID)
                    .setStmt(ByteString.copyFrom(stmt, charset))
                    .build();

            return stub.withDeadlineAfter(timeout, TimeUnit.MILLISECONDS).execute(request);
        } catch (Exception e) {
            if (e instanceof StatusRuntimeException
                    && (((StatusRuntimeException) e)
                    .getStatus()
                    .getCode() == Code.DEADLINE_EXCEEDED)) {
                throw new IOErrorException(E_TIME_OUT,
                                           String.format("request to %s timeout after %dms",
                                                         serverAddr.toString(),
                                                         timeout));
            }
            throw e;
        }
    }

    public ExecuteResponse execute(long sessionID, String stmt) throws IOErrorException {
        return execute(sessionID, stmt, this.requestTimeout);
    }
}
