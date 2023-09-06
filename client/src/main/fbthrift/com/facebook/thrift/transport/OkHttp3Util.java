/* Copyright (c) 2023 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.facebook.thrift.transport;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class OkHttp3Util {
    private static OkHttpClient client;

    private OkHttp3Util() {
    }

    public static OkHttpClient getClient(int connectTimeout, int readTimeout,
                                         SSLSocketFactory sslFactory,
                                         TrustManager trustManager) {
        if (client == null) {
            synchronized (OkHttp3Util.class) {
                if (client == null) {
                    // Create OkHttpClient builder
                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                            .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
                            .readTimeout(readTimeout, TimeUnit.MILLISECONDS);
                    if (sslFactory != null) {
                        clientBuilder.sslSocketFactory(sslFactory, (X509TrustManager) trustManager);
                        clientBuilder.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1));
                    } else {
                        // config the http2 prior knowledge
                        clientBuilder.protocols(Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE));
                    }
                    client = clientBuilder.build();
                }
            }
        }
        return client;
    }

    public static void close(){
        if (client != null) {
            client.connectionPool().evictAll();
            client.dispatcher().executorService().shutdown();
            client = null;
        }
    }
}
