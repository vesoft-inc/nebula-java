package com.vesoft.nebula.client.graph.pool.config;

import com.vesoft.nebula.client.graph.NebulaGraphConnection;
import com.vesoft.nebula.client.graph.pool.entity.LinkDomain;
import java.util.List;
import java.util.Objects;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaConnectionPoolConfig is used for
 * @Date 2020/3/24 - 15:42
 */
public class NebulaConnectionPoolConfig extends GenericObjectPoolConfig<NebulaGraphConnection> {

    private int maxIdle = 20;

    private int minIdle = 2;

    private int maxTotal = 50;

    /**
     * connection addresses
     */
    private List<LinkDomain> addresses;
    /**
     * retry connect time
     */
    private int connectionRetry = 3;

    /**
     * socket timeout
     */
    private int timeout = 10000;


    public NebulaConnectionPoolConfig(List<LinkDomain> addresses) {
        this.addresses = addresses;
    }

    public NebulaConnectionPoolConfig(List<LinkDomain> addresses,
                                      int connectionRetry, int timeout) {
        this.addresses = addresses;
        this.connectionRetry = connectionRetry;
        this.timeout = timeout;
    }

    public NebulaConnectionPoolConfig(List<LinkDomain> addresses,
                                      int connectionRetry, int timeout,
                                      int maxIdle, int minIdle,
                                      int maxTotal) {
        this.addresses = addresses;
        this.connectionRetry = connectionRetry;
        this.timeout = timeout;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.maxTotal = maxTotal;
    }


    @Override
    public int getMaxIdle() {
        return maxIdle;
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    @Override
    public int getMinIdle() {
        return minIdle;
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    @Override
    public int getMaxTotal() {
        return maxTotal;
    }

    @Override
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public List<LinkDomain> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<LinkDomain> addresses) {
        this.addresses = addresses;
    }

    public int getConnectionRetry() {
        return connectionRetry;
    }

    public void setConnectionRetry(int connectionRetry) {
        this.connectionRetry = connectionRetry;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NebulaConnectionPoolConfig that = (NebulaConnectionPoolConfig) o;
        return maxIdle == that.maxIdle
                && minIdle == that.minIdle
                && maxTotal == that.maxTotal
                && connectionRetry == that.connectionRetry
                && timeout == that.timeout
                && Objects.equals(addresses, that.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxIdle, minIdle, maxTotal,
                addresses, connectionRetry, timeout);
    }

    @Override
    public String toString() {
        return "NebulaConnectionPoolConfig{" + "maxTotal=" + maxTotal
                + ", addresses=" + addresses
                + ", connectionRetry=" + connectionRetry
                + '}';
    }
}
