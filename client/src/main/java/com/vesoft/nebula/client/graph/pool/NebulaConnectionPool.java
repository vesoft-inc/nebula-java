package com.vesoft.nebula.client.graph.pool;

import com.vesoft.nebula.client.graph.NebulaConnection;
import java.util.Objects;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaConnectionPool is used for
 * @Date 2020/3/24 - 15:43
 */
public class NebulaConnectionPool extends GenericObjectPool<NebulaConnection> {

    private NebulaPoolConnectionFactory factory;

    public NebulaConnectionPool(NebulaPoolConnectionFactory factory) {
        super((BasePooledObjectFactory) factory, factory.getConfig());
        this.factory = factory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NebulaConnectionPool that = (NebulaConnectionPool) o;
        return Objects.equals(factory, that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factory);
    }
}
