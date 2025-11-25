/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import java.io.Serializable;
import javax.net.ssl.SSLException;

public abstract class Connection implements Serializable {

    private static final long serialVersionUID = -8425216612015802331L;

    protected HostAddress serverAddr = null;

    public HostAddress getServerAddress() {
        return this.serverAddr;
    }

    public abstract void open(HostAddress address,
                              NebulaClient.Builder builder) throws IOErrorException;


    public abstract void close();

    public abstract boolean ping(long sessionID, long timeoutMs) throws IOErrorException;
}
