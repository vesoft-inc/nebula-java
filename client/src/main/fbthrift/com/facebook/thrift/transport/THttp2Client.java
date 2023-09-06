/* Copyright (c) 2023 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.facebook.thrift.transport;

import com.facebook.thrift.utils.Logger;
import java.io.ByteArrayOutputStream;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class THttp2Client extends TTransport {
    private static final Logger LOGGER = Logger.getLogger(THttp2Client.class.getName());

    private final ByteArrayOutputStream requestBuffer = new ByteArrayOutputStream();
    private ResponseBody responseBody = null;
    private Map<String, String> customHeaders = null;
    private static final Map<String, String> defaultHeaders = getDefaultHeaders();

    private OkHttpClient client;
    private final SSLSocketFactory sslFactory;

    private final TrustManager trustManager;
    private final String url;
    private int connectTimeout = 0;
    private int readTimeout = 0;


    public THttp2Client(String url) throws TTransportException {
        this(url, null, null);
    }

    public THttp2Client(String url, SSLSocketFactory sslFactory, TrustManager trustManager) throws TTransportException {
        this.url = url;
        this.sslFactory = sslFactory;
        this.trustManager = trustManager;
    }

    public THttp2Client setConnectTimeout(int timeout) {
        connectTimeout = timeout;
        return this;
    }

    public THttp2Client setReadTimeout(int timeout) {
        readTimeout = timeout;
        return this;
    }

    public THttp2Client setCustomHeaders(Map<String, String> headers) {
        customHeaders = headers;
        return this;
    }

    public THttp2Client setCustomHeader(String key, String value) {
        if (customHeaders == null) {
            customHeaders = new HashMap<>();
        }
        customHeaders.put(key, value);
        return this;
    }

    public void open() {
        client = OkHttp3Util.getClient(connectTimeout, readTimeout, sslFactory, trustManager);
    }

    public void close() {
        try {
            if (responseBody != null) {
                responseBody.close();
                responseBody = null;
            }

            requestBuffer.close();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        OkHttp3Util.close();
    }

    public boolean isOpen() {
        return client != null;
    }

    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (responseBody == null) {
            throw new TTransportException("Response buffer is empty, no request.");
        }
        try {
            int ret = responseBody.byteStream().read(buf, off, len);
            if (ret == -1) {
                throw new TTransportException("No more data available.");
            }
            return ret;
        } catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }

    public void write(byte[] buf, int off, int len) {
        requestBuffer.write(buf, off, len);
    }

    public void flush() throws TTransportException {
        if (null == client) {
            throw new TTransportException("Null HttpClient, aborting.");
        }

        // Extract request and reset buffer
        byte[] data = requestBuffer.toByteArray();
        requestBuffer.reset();
        try {

            // Create request object
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/x-thrift"), data));

            defaultHeaders.forEach(requestBuilder::header);
            if (customHeaders != null) {
                customHeaders.forEach(requestBuilder::header);
            }

            Request request = requestBuilder.build();

            // Make the request
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new TTransportException("HTTP Response code: " + response.code());
            }

            // Read the response
            responseBody = response.body();
        } catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }

    private static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-thrift");
        headers.put("Accept", "application/x-thrift");
        headers.put("User-Agent", "Java/THttpClient");
        return headers;
    }
}
