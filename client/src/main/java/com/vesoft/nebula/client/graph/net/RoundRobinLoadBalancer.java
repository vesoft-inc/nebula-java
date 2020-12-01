package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RoundRobinLoadBalancer implements LoadBalancer {
    private static final int S_OK = 0;
    private static final int S_BAD = 1;
    private final List<HostAddress> addresses = new ArrayList<>();
    private final Map<HostAddress, Integer> serversStatus = new HashMap<>();
    private final int timeout;
    private final AtomicInteger pos = new AtomicInteger(0);
    private final int delayTime = 60;  // unit seconds
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout) {
        this.timeout = timeout;
        for (HostAddress addr : addresses) {
            this.addresses.add(addr);
            this.serversStatus.put(addr, S_BAD);
        }
        schedule.scheduleAtFixedRate(this::scheduleTask, 0, delayTime, TimeUnit.SECONDS);
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
        for (HostAddress addr : addresses) {
            if (ping(addr)) {
                serversStatus.put(addr, S_OK);
            } else {
                serversStatus.put(addr, S_BAD);
            }
        }
    }

    public boolean ping(HostAddress addr) {
        try {
            Connection connection = new SyncConnection();
            connection.open(addr, this.timeout);
            connection.close();
            return true;
        } catch (IOErrorException e) {
            return false;
        }
    }

    public boolean isServersOK() {
        this.updateServersStatus();
        for (HostAddress addr : addresses) {
            if (serversStatus.get(addr) == S_BAD) {
                return false;
            }
        }
        return true;
    }

    private void scheduleTask() {
        updateServersStatus();
    }
}
