/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula;

import com.vesoft.nebula.driver.v3client.graph.NebulaPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.net.NebulaPool;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates the v3-compatible client surface running against NebulaGraph v5.
 *
 * <p>This is a migration example: it uses the v3 client API shape (NebulaPool + Session +
 * ResultSet) from the {@code com.vesoft.nebula.driver.v3client} namespace, and only the GQL
 * statements follow the v5 ISO-GQL dialect.
 */
public class V3CompatExample {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: V3CompatExample <address> <user> <password>");
            System.exit(1);
        }
        String address = args[0];
        String user = args[1];
        String password = args[2];

        List<HostAddress> addresses =
            Arrays.asList(new HostAddress(address.split(":")[0],
                                          Integer.parseInt(address.split(":")[1])));

        NebulaPoolConfig poolConfig = new NebulaPoolConfig();
        poolConfig.setMaxConnSize(10);
        poolConfig.setMinConnSize(0);
        poolConfig.setTimeout(1000);

        NebulaPool pool = new NebulaPool();
        try {
            pool.init(addresses, poolConfig);
            Session session = pool.getSession(user, password, false);
            ResultSet resultSet = session.execute("RETURN 1+1 AS result");
            if (!resultSet.isSucceeded()) {
                System.out.println("Query failed: " + resultSet.getErrorMessage());
                session.release();
                pool.close();
                System.exit(1);
            }
            if (resultSet.rowsSize() > 0) {
                System.out.println("result = "
                                       + resultSet.rowValues(0).get("result").asLong());
            }
            session.release();
            pool.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
