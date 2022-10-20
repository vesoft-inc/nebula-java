package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);
    private static final int S_OK = 0;
    private static final int S_BAD = 1;
    private final List<HostAddress> addresses = new ArrayList<>();
    private final Map<HostAddress, Integer> serversStatus = new ConcurrentHashMap<>();
    private final double minClusterHealthRate;
    private final int timeout;
    private final AtomicInteger pos = new AtomicInteger(0);
    private final int delayTime = 60;  // Unit seconds
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
    private SSLParam sslParam;
    private boolean enabledSsl;

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout,
                                  double minClusterHealthRate) {
        this.timeout = timeout;
        for (HostAddress addr : addresses) {
            this.addresses.add(addr);
            this.serversStatus.put(addr, S_BAD);
        }
        this.minClusterHealthRate = minClusterHealthRate;
        schedule.scheduleAtFixedRate(this::scheduleTask, 0, delayTime, TimeUnit.SECONDS);
    }

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout, SSLParam sslParam,
                                  double minClusterHealthRate) {
        this(addresses, timeout, minClusterHealthRate);
        this.sslParam = sslParam;
        this.enabledSsl = true;
    }

    public void close() {
        schedule.shutdownNow();
    }

    @Override
    public HostAddress getAddress() {
        // TODO: update the server connection num into load balancer
        int tryCount = 0;
        int newPos;
        while (++tryCount <= addresses.size()) {
            newPos = (pos.getAndIncrement()) % addresses.size();
            HostAddress addr = addresses.get(newPos);
            if (serversStatus.get(addr) == S_OK) {
                return addr;
            }
        }
        return null;
    }

    public void updateServersStatus() {
        for (HostAddress hostAddress : addresses) {
            if (ping(hostAddress)) {
                serversStatus.put(hostAddress, S_OK);
            } else {
                serversStatus.put(hostAddress, S_BAD);
            }
        }
    }

    public boolean ping(HostAddress addr) {
        try {
            Connection connection = new SyncConnection();
            if (enabledSsl) {
                connection.open(addr, this.timeout, sslParam);
            } else {
                connection.open(addr, this.timeout);
            }
            boolean pong = connection.ping();
            connection.close();
            return pong;
        } catch (IOErrorException e) {
            return false;
        } catch (ClientServerIncompatibleException e) {
            LOGGER.error("version verify failed, ", e);
            return false;
        }
    }

    public boolean isServersOK() {
        this.updateServersStatus();
        double numServersWithOkStatus = 0;
        for (HostAddress hostAddress : addresses) {
            if (serversStatus.get(hostAddress) == S_OK) {
                numServersWithOkStatus++;
            }
        }

        // Check health rate.
        double okServersRate = numServersWithOkStatus / addresses.size();
        return okServersRate >= minClusterHealthRate;
    }

    private void scheduleTask() {
        updateServersStatus();
    }
}
