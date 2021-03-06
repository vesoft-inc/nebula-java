/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;

public class PartScanInfo implements Serializable {

    private static final long serialVersionUID = 1969725091044874463L;

    private int part;
    private HostAddress leader;
    private byte[] cursor = null;

    public PartScanInfo(int part, HostAddress leader) {
        this.part = part;
        this.leader = leader;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public HostAddress getLeader() {
        return leader;
    }

    public void setLeader(HostAddress leader) {
        this.leader = leader;
    }

    public byte[] getCursor() {
        return cursor;
    }

    public void setCursor(byte[] cursor) {
        this.cursor = cursor;
    }

    @Override
    public String toString() {
        return "PartScanInfo{"
                + "part=" + part
                + ", leader=" + leader
                + ", cursor=" + new String(cursor)
                + '}';
    }
}
