/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import java.io.Serializable;
import java.util.List;

public class SessionPoolConfig implements Serializable {

    private static final long serialVersionUID = -2266013330384849132L;

    private String username;

    private String password;
    private List<HostAddress> graphAddressList;
    private String spaceName;

    // The min connections in pool for all addresses
    private int minConnsSize = 0;

    // The max connections in pool for all addresses
    private int maxConnsSize = 10;

    // The min connections in pool for all addresses
    private int minSessionSize = 0;

    // The max connections in pool for all addresses
    private int maxSessionSize = 10;

    // Socket timeout and Socket connection timeout, unit: millisecond
    private int timeout = 0;

    // The idleTime of the connection, unit: millisecond
    // The connection's idle time more than idleTime, it will be delete
    // 0 means never delete
    private int idleTime = 0;

    // The interval time to check idle connection, unit ms, -1 means no check
    private int intervalIdle = -1;

    // The wait time to get idle connection, unit ms
    private int waitTime = 0;

    // The minimum rate of healthy servers to all servers. if 1 it means all servers should be
    // available on init.
    private double minClusterHealthRate = 1;

    // Set to true to turn on ssl encrypted traffic
    private boolean enableSsl = false;

    // SSL param is required if ssl is turned on
    private SSLParam sslParam = null;

    private boolean reConnect = true;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<HostAddress> getGraphAddressList() {
        return graphAddressList;
    }

    public void setGraphAddressList(List<HostAddress> graphAddressList) {
        this.graphAddressList = graphAddressList;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public int getMinConnsSize() {
        return minConnsSize;
    }

    public void setMinConnsSize(int minConnsSize) {
        this.minConnsSize = minConnsSize;
    }

    public int getMaxConnsSize() {
        return maxConnsSize;
    }

    public void setMaxConnsSize(int maxConnsSize) {
        this.maxConnsSize = maxConnsSize;
    }

    public int getMinSessionSize() {
        return minSessionSize;
    }

    public void setMinSessionSize(int minSessionSize) {
        this.minSessionSize = minSessionSize;
    }

    public int getMaxSessionSize() {
        return maxSessionSize;
    }

    public void setMaxSessionSize(int maxSessionSize) {
        this.maxSessionSize = maxSessionSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getIntervalIdle() {
        return intervalIdle;
    }

    public void setIntervalIdle(int intervalIdle) {
        this.intervalIdle = intervalIdle;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public double getMinClusterHealthRate() {
        return minClusterHealthRate;
    }

    public void setMinClusterHealthRate(double minClusterHealthRate) {
        this.minClusterHealthRate = minClusterHealthRate;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public SSLParam getSslParam() {
        return sslParam;
    }

    public void setSslParam(SSLParam sslParam) {
        this.sslParam = sslParam;
    }

    public boolean isReConnect() {
        return reConnect;
    }

    public void setReConnect(boolean reConnect) {
        this.reConnect = reConnect;
    }

    @Override
    public String toString() {
        return "SessionPoolConfig{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", graphAddressList=" + graphAddressList +
                ", spaceName='" + spaceName + '\'' +
                ", minConnsSize=" + minConnsSize +
                ", maxConnsSize=" + maxConnsSize +
                ", minSessionSize=" + minSessionSize +
                ", maxSessionSize=" + maxSessionSize +
                ", timeout=" + timeout +
                ", idleTime=" + idleTime +
                ", intervalIdle=" + intervalIdle +
                ", waitTime=" + waitTime +
                ", minClusterHealthRate=" + minClusterHealthRate +
                ", enableSsl=" + enableSsl +
                ", sslParam=" + sslParam +
                ", reConnect=" + reConnect +
                '}';
    }
}
