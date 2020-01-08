/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import java.util.Iterator;

public class NebulaRowSet implements Iterator<NebulaRow> {

    /**
     *
     */
    public NebulaRowSet() {

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public NebulaRow next() {
        return null;
    }

    @Override
    public void remove() {

    }
}
