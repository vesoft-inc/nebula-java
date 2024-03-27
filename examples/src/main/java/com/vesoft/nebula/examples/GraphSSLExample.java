/* Copyright (c) 2024 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * if you trust server's self sign certificate, import the root certificate who issues
 * server certificate into client system trust store using keytool:
 *     keytool -import -trustcacerts -alias root_ca -file Desktop/root.crt -keystore truststore.jks
 * And load the local truststore.jks into system:
 *     System.setProperty("javax.net.ssl.trustStore", "/Users/nicole/truststore.jks");
 *     System.setProperty("javax.net.ssl.trustStorePassword", "123456");
 */

public class GraphSSLExample {
    public static void main(String[] args) throws UnknownHostException, IOErrorException,
            AuthFailedException, ClientServerIncompatibleException, NotValidConnectionException {
        // if server use the certificate issued by CA , then please make sure your client trust
        // store has been load in System.
        String trustStorePath = System.getenv("JAVA_HOME") + "/jre/lib/security/cacerts";
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        // default password of JDK cacerts is changeit
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");


        NebulaPoolConfig nebulaSslPoolConfig = new NebulaPoolConfig();
        nebulaSslPoolConfig.setMaxConnSize(100);
        // enable ssl
        nebulaSslPoolConfig.setEnableSsl(true);
        // config ssl is CA and config no certificate
        nebulaSslPoolConfig.setSslParam(new CASignedSSLParam());

        NebulaPool sslPool = new NebulaPool();
        sslPool.init(Arrays.asList(new HostAddress(
                "nebula-graph-nco13931ssfnnd8o6bk50.aws.dev.cloud.nebula-graph.io", 9669)),
                nebulaSslPoolConfig);
        String query = "YIELD 1";
        Session sslSession = sslPool.getSession("dbaas-test@vesoft.com",
                "H3prCPAh1POJEgxPkReEQQ", false);
        ResultSet resp = sslSession.execute(query);

        if (!resp.isSucceeded()) {
            System.out.println(
                    String.format("Execute: `%s', failed: %s", query, resp.getErrorMessage()));
            System.exit(1);
        }
        System.out.println(resp);
        sslSession.release();
        sslPool.close();
    }
}
