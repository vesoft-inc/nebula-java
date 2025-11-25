/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.utils;

import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.scan.ScanEdgeResult;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TlsUtil {

    public static SslContext getSslContext(boolean disableVerifyServerCert,
                                           String ca,
                                           String cert,
                                           String key) throws SSLException {
        if (disableVerifyServerCert) {
            return GrpcSslContexts
                .forClient()
                .protocols("TLSv1.2")
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        }

        File caFile = new File(ca);
        SslContextBuilder builder = GrpcSslContexts
            .forClient()
            .protocols("TLSv1.2")
            .trustManager(caFile);
        if (cert != null && key != null) {
            builder.keyManager(new File(cert), new File(key));
        }
        return builder.build();
    }
}
