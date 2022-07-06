package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use when query latency is required
 */
public class PersistenceSessionPool {

    private Queue<Session> queue;
    private String userName;
    private String passWord;
    private int minCountSession;
    private int maxCountSession;
    private NebulaPool pool;
    private static final Logger log = LoggerFactory.getLogger(PersistenceSessionPool.class);

    public PersistenceSessionPool(NebulaPoolConfig nebulaPoolConfig, String hostAndPort,
        String userName, String passWord)
        throws UnknownHostException, NotValidConnectionException, IOErrorException,
        AuthFailedException, ClientServerIncompatibleException {
        this.minCountSession = nebulaPoolConfig.getMinConnSize();
        this.maxCountSession = nebulaPoolConfig.getMaxConnSize();
        this.userName = userName;
        this.passWord = passWord;
        this.queue = new LinkedBlockingQueue<>(minCountSession);
        this.pool = this.initGraphClient(hostAndPort, nebulaPoolConfig);
        initSession();
    }

    public Session borrow() {
        Session se = queue.poll();
        if (se != null) {
            return se;
        }
        try {
            return this.pool.getSession(userName, passWord, true);
        } catch (Exception e) {
            log.error("execute borrow session fail, detail: ", e);
            throw new RuntimeException(e);
        }
    }

    public void release(Session se) {
        if (se != null) {
            boolean success = queue.offer(se);
            if (!success) {
                se.release();
            }
        }
    }

    public void close() {
        this.pool.close();
    }

    private void initSession()
        throws NotValidConnectionException, IOErrorException, AuthFailedException,
        ClientServerIncompatibleException {
        for (int i = 0; i < minCountSession; i++) {
            queue.offer(this.pool.getSession(userName, passWord, true));
        }
    }

    private NebulaPool initGraphClient(String hostAndPort, NebulaPoolConfig nebulaPoolConfig)
        throws UnknownHostException {
        List<HostAddress> hostAndPorts = getGraphHostPort(hostAndPort);
        NebulaPool pool = new NebulaPool();
        pool.init(hostAndPorts, nebulaPoolConfig);
        return pool;
    }

    private List<HostAddress> getGraphHostPort(String hostAndPort) {
        String[] split = hostAndPort.split(",");
        return Arrays.stream(split).map(item -> {
            String[] splitList = item.split(":");
            return new HostAddress(splitList[0], Integer.parseInt(splitList[1]));
        }).collect(Collectors.toList());
    }
}
