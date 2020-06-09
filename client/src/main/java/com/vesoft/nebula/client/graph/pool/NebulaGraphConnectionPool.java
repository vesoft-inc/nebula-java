/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.pool;

import com.vesoft.nebula.client.graph.NebulaGraphConnection;
import java.util.Objects;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaGraphConnectionPool is used for
 * @Date 2020/3/24 - 15:43
 */
public class NebulaGraphConnectionPool extends GenericObjectPool<NebulaGraphConnection> {

    private NebulaGraphPoolConnectionFactory factory;

    public NebulaGraphConnectionPool(NebulaGraphPoolConnectionFactory factory) {
        super((BasePooledObjectFactory) factory, factory.getConfig());
        this.factory = factory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NebulaGraphConnectionPool that = (NebulaGraphConnectionPool) o;
        return Objects.equals(this.factory, that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.factory);
    }
}
