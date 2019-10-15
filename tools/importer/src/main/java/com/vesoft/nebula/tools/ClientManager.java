package com.vesoft.nebula.tools;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.graph.client.GraphClient;
import com.vesoft.nebula.graph.client.GraphClientImpl;
import org.apache.log4j.Logger;

import java.util.List;

public class ClientManager {
    private static final Logger LOGGER = Logger.getLogger(ClientManager.class.getClass());

    private static ThreadLocal<GraphClient> clientThreadLocal = new ThreadLocal<>();

    public static GraphClient getClient(List<HostAndPort> hostAndPorts, GeoOptions options) throws Exception {
        GraphClient client = clientThreadLocal.get();
        if (client == null) {
            client = new GraphClientImpl(hostAndPorts, options.timeout, 3, 1);
            if (client.connect(options.user, options.password) != 0) {
                throw new Exception("Connect fail.");
            }
            if (client.execute(String.format(Constant.USE_TEMPLATE, options.spaceName)) != 0) {
                throw new Exception("Switch space fail.");
            }
            LOGGER.info(Thread.currentThread().getName() + ": switch to space " + options.spaceName);
            clientThreadLocal.set(client);
        }

        return client;
    }
}
