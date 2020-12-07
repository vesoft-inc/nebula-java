/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.IOErrorException;

public abstract class Connection {
    protected HostAddress serverAddr = null;

    public HostAddress getServerAddress() {
        return this.serverAddr;
    }

    public abstract void open(HostAddress address, int timeout) throws IOErrorException;

    public abstract void close();

    public abstract boolean ping();
}
