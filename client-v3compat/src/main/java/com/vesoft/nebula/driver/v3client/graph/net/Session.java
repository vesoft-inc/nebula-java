/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.v3client.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A session against NebulaGraph, matching the v3 client's {@code Session} API.
 *
 * <p>Internally it delegates to a v5 driver {@link com.vesoft.nebula.driver.graph.net.NebulaClient};
 * one v5 client is one server-side session.
 */
public class Session implements Serializable, AutoCloseable {

    private static final long serialVersionUID = -8855886967097862376L;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final long sessionID;
    private final int timezoneOffset;
    private final com.vesoft.nebula.driver.graph.net.NebulaPool pool;
    private final boolean retryConnect;
    private final AtomicBoolean released = new AtomicBoolean(false);
    private volatile com.vesoft.nebula.driver.graph.net.NebulaClient client;

    public Session(com.vesoft.nebula.driver.graph.net.NebulaClient client,
                   com.vesoft.nebula.driver.graph.net.NebulaPool pool,
                   boolean retryConnect) {
        this.client = client;
        this.sessionID = client.getSessionId();
        this.timezoneOffset = 0;
        this.pool = pool;
        this.retryConnect = retryConnect;
    }

    public synchronized ResultSet execute(String stmt) throws IOErrorException {
        return executeWithParameter(stmt, Collections.emptyMap());
    }

    public synchronized ResultSet executeWithParameter(String stmt, Map<String, Object> parameterMap)
        throws IOErrorException {
        checkReleased();
        String gql = inlineParameters(stmt, parameterMap);
        try {
            return new ResultSet(client.execute(gql), timezoneOffset);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            if (isConnectBroken(e) && retryConnect && reconnect()) {
                try {
                    return new ResultSet(client.execute(gql), timezoneOffset);
                } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e2) {
                    throw toCompat(e2);
                }
            }
            throw toCompat(e);
        }
    }

    public ResultSet executeWithTimeout(String stmt, long timeoutMs) throws IOErrorException {
        return executeWithParameterTimeout(stmt, Collections.emptyMap(), timeoutMs);
    }

    public synchronized ResultSet executeWithParameterTimeout(String stmt,
                                                              Map<String, Object> parameterMap,
                                                              long timeoutMs)
        throws IOErrorException {
        checkReleased();
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeout should be a positive number");
        }
        String gql = inlineParameters(stmt, parameterMap);
        try {
            return new ResultSet(client.execute(gql, timeoutMs), timezoneOffset);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            if (isConnectBroken(e) && retryConnect && reconnect()) {
                try {
                    return new ResultSet(client.execute(gql, timeoutMs), timezoneOffset);
                } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e2) {
                    throw toCompat(e2);
                }
            }
            throw toCompat(e);
        }
    }

    public synchronized String executeJson(String stmt) throws IOErrorException {
        return executeJsonWithParameter(stmt, Collections.emptyMap());
    }

    public synchronized String executeJsonWithParameter(String stmt,
                                                        Map<String, Object> parameterMap)
        throws IOErrorException {
        ResultSet resultSet = executeWithParameter(stmt, parameterMap);
        return toJson(resultSet);
    }

    public synchronized boolean ping() {
        if (client == null) {
            return false;
        }
        return client.ping();
    }

    public synchronized boolean pingSession() {
        if (client == null) {
            return false;
        }
        return client.ping();
    }

    public synchronized void release() {
        if (client == null) {
            return;
        }
        if (released.compareAndSet(false, true)) {
            if (pool != null) {
                pool.returnClient(client);
            } else {
                client.close();
            }
            client = null;
        }
    }

    public synchronized HostAddress getGraphHost() {
        if (client == null) {
            return null;
        }
        return parseHost(client.getHost());
    }

    public long getSessionID() {
        return sessionID;
    }

    @Override
    public synchronized void close() {
        release();
    }

    private void checkReleased() throws IOErrorException {
        if (client == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                                       "The session was released, couldn't use again.");
        }
    }

    private boolean reconnect() {
        if (pool == null) {
            return false;
        }
        client.close();
        pool.returnClient(client);
        try {
            client = pool.getClient();
            return true;
        } catch (Exception e) {
            log.error("Reconnect failed: " + e);
            return false;
        }
    }

    public static AuthFailedException toCompatAuth(
        com.vesoft.nebula.driver.graph.exception.AuthFailedException e) {
        String msg = e.getMessage();
        String prefix = "Auth failed: ";
        if (msg != null && msg.startsWith(prefix)) {
            msg = msg.substring(prefix.length());
        }
        return new AuthFailedException(msg);
    }

    public static boolean isConnectBroken(
        com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
        return e.getType() == com.vesoft.nebula.driver.graph.exception.IOErrorException
            .E_CONNECT_BROKEN;
    }

    public static IOErrorException toCompat(
        com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
        int type;
        switch (e.getType()) {
            case com.vesoft.nebula.driver.graph.exception.IOErrorException.E_CONNECT_BROKEN:
                type = IOErrorException.E_CONNECT_BROKEN;
                break;
            case com.vesoft.nebula.driver.graph.exception.IOErrorException.E_ALL_BROKEN:
                type = IOErrorException.E_ALL_BROKEN;
                break;
            case com.vesoft.nebula.driver.graph.exception.IOErrorException.E_TIME_OUT:
                type = IOErrorException.E_TIME_OUT;
                break;
            case com.vesoft.nebula.driver.graph.exception.IOErrorException.E_NO_OPEN:
                type = IOErrorException.E_NO_OPEN;
                break;
            default:
                type = IOErrorException.E_UNKNOWN;
        }
        return new IOErrorException(type, e.getMessage());
    }

    public static String toJson(ResultSet resultSet) {
        JSONObject root = new JSONObject();
        JSONArray errors = new JSONArray();
        JSONObject error = new JSONObject();
        error.put("code", resultSet.getErrorCode());
        error.put("message", resultSet.getErrorMessage());
        errors.add(error);
        root.put("errors", errors);

        JSONArray results = new JSONArray();
        JSONObject result = new JSONObject();
        result.put("columns", resultSet.getColumnNames());
        result.put("latencyInUs", resultSet.getLatency());
        result.put("spaceName", resultSet.getSpaceName());
        result.put("comment", resultSet.getComment());
        JSONArray data = new JSONArray();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            JSONObject rowObj = new JSONObject();
            JSONArray row = new JSONArray();
            for (ValueWrapper value : resultSet.rowValues(i)) {
                row.add(unquote(value));
            }
            rowObj.put("row", row);
            rowObj.put("meta", JSON.parseObject("{}"));
            data.add(rowObj);
        }
        result.put("data", data);
        results.add(result);
        root.put("results", results);
        return JSON.toJSONString(root);
    }

    private static Object unquote(ValueWrapper value) {
        String str = value.toString();
        if (str != null && str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * Inline {@code $param} placeholders with GQL literals (the v5 execute RPC takes no parameter
     * map). Keys are replaced longest-first to avoid prefix collisions.
     */
    public static String inlineParameters(String stmt, Map<String, Object> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return stmt;
        }
        String gql = stmt;
        List<String> keys = new ArrayList<>(parameterMap.keySet());
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));
        for (String key : keys) {
            gql = gql.replace("$" + key, value2GqlLiteral(parameterMap.get(key)));
        }
        return gql;
    }

    /**
     * Convert a Java value to a GQL literal. Supports the v3 client's parameter value types:
     * null, boolean, numeric, string, bytes, list/collection, map, and falls back to a quoted
     * string for other types.
     */
    public static String value2GqlLiteral(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Boolean
            || value instanceof Integer
            || value instanceof Short
            || value instanceof Byte
            || value instanceof Long
            || value instanceof Float
            || value instanceof Double) {
            return String.valueOf(value);
        }
        if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        }
        if (value instanceof byte[]) {
            return "\"" + escape(new String((byte[]) value, StandardCharsets.UTF_8)) + "\"";
        }
        if (value instanceof Character) {
            return "\"" + escape(value.toString()) + "\"";
        }
        if (value instanceof Collection) {
            List<String> literals = new ArrayList<>();
            for (Object element : (Collection<?>) value) {
                literals.add(value2GqlLiteral(element));
            }
            return "[" + String.join(", ", literals) + "]";
        }
        if (value instanceof Map) {
            List<String> entries = new ArrayList<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                entries.add(entry.getKey().toString() + ": " + value2GqlLiteral(entry.getValue()));
            }
            return "{" + String.join(", ", entries) + "}";
        }
        return "\"" + escape(value.toString()) + "\"";
    }

    private static String escape(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\'':
                    builder.append("\\'");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    public static HostAddress parseHost(String host) {
        if (host == null || host.isEmpty()) {
            return null;
        }
        if (host.startsWith("[")) {
            int close = host.indexOf(']');
            if (close < 0) {
                return new HostAddress(host, 0);
            }
            return new HostAddress(host.substring(1, close),
                                   Integer.parseInt(host.substring(close + 2)));
        }
        int idx = host.lastIndexOf(':');
        if (idx < 0) {
            return new HostAddress(host, 0);
        }
        return new HostAddress(host.substring(0, idx), Integer.parseInt(host.substring(idx + 1)));
    }
}
