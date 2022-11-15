/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Duration;
import com.vesoft.nebula.Geography;
import com.vesoft.nebula.NList;
import com.vesoft.nebula.NMap;
import com.vesoft.nebula.NullType;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ExecutionResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Session is an object that operates with nebula-graph.
 * It provides an interface  `execute` to send any NGQL.
 * The returned data result `ResultSet` include wrapped string encoding
 * and time zone calculations and Node and Relationship and PathWrapper
 * and DateWrapper and TimeWrapper and DateTimeWrapper.
 * The data type obtained by the user is `ValueWrapper`,
 * which is the wrapper of the original data structure Value returned by the server.
 * The user can directly read the data using the interface of ValueWrapper.
 */

public class Session implements Serializable, AutoCloseable {

    private static final long serialVersionUID = -8855886967097862376L;

    private final long sessionID;
    private final int timezoneOffset;
    private SyncConnection connection;
    private final NebulaPool pool;
    private final Boolean retryConnect;
    private final AtomicBoolean connectionIsBroken = new AtomicBoolean(false);
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor
     *
     * @param connection   the connection from the pool
     * @param authResult   the auth result from graph service
     * @param connPool     the connection pool
     * @param retryConnect whether to retry after the connection is disconnected
     */
    public Session(SyncConnection connection,
                   AuthResult authResult,
                   NebulaPool connPool,
                   Boolean retryConnect) {
        this.connection = connection;
        this.sessionID = authResult.getSessionId();
        this.timezoneOffset = authResult.getTimezoneOffset();
        this.pool = connPool;
        this.retryConnect = retryConnect;
    }

    /**
     * Execute the nGql sentence.
     *
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     * @return The ResultSet
     */
    public synchronized ResultSet execute(String stmt) throws
            IOErrorException {
        return executeWithParameter(stmt,
                (Map<String, Object>) Collections.EMPTY_MAP);
    }

    /**
     * Execute the nGql sentence.
     *
     * @param stmt         The nGql sentence.
     *                     such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     * @param parameterMap The nGql parameter map
     * @return The ResultSet
     */
    public synchronized ResultSet executeWithParameter(
            String stmt,
            Map<String, Object> parameterMap)
            throws IOErrorException {
        if (connection == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                    "The session was released, couldn't use again.");
        }
        Map<byte[], Value> map = new HashMap<>();
        parameterMap.forEach((key, value) -> map.put(key.getBytes(), value2Nvalue(value)));

        if (connectionIsBroken.get() && retryConnect) {
            if (retryConnect()) {
                ExecutionResponse resp =
                        connection.executeWithParameter(sessionID, stmt, map);
                return new ResultSet(resp, timezoneOffset);
            } else {
                throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                        "All servers are broken.");
            }
        }

        try {
            ExecutionResponse resp = connection.executeWithParameter(sessionID, stmt, map);
            return new ResultSet(resp, timezoneOffset);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                connectionIsBroken.set(true);
                pool.updateServerStatus();

                if (retryConnect) {
                    if (retryConnect()) {
                        connectionIsBroken.set(false);
                        ExecutionResponse resp =
                                connection.executeWithParameter(sessionID, stmt, map);
                        return new ResultSet(resp, timezoneOffset);
                    } else {
                        connectionIsBroken.set(true);
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
            }
            throw ie;
        }
    }

    /**
     * Execute the nGql sentence.
     * Date and Datetime will be returned in UTC
     * JSON struct:
     *             {
     *               "results":[
     *                 {
     *                   "columns":[],
     *                   "data":[
     *                     {
     *                       "row":row-data,
     *                       "meta":metadata
     *                     }
     *                   ],
     *                   "latencyInUs":0,
     *                   "spaceName":"",
     *                   "planDesc ":{
     *                     "planNodeDescs":[
     *                       {
     *                         "name":"",
     *                         "id":0,
     *                         "outputVar":"",
     *                         "description":{
     *                           "key":""
     *                         },
     *                         "profiles":[
     *                           {
     *                             "rows":1,
     *                             "execDurationInUs":0,
     *                             "totalDurationInUs":0,
     *                             "otherStats":{}
     *                           }
     *                         ],
     *                         "branchInfo":{
     *                           "isDoBranch":false,
     *                           "conditionNodeId":-1
     *                         },
     *                         "dependencies":[]
     *                       }
     *                     ],
     *                     "nodeIndexMap":{},
     *                     "format":"",
     *                     "optimize_time_in_us":0
     *                   },
     *                   "comment ":"",
     *                 }
     *               ],
     *              "errors":[
     *                 {
     *                   "code": 0,
     *                   "message": ""
     *                 }
     *               ]
     *             }
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     *        parameterMap The nGql parameters
     * @return The JSON string
     */
    public synchronized String executeJson(String stmt) throws
            IOErrorException {
        return executeJsonWithParameter(stmt,
                (Map<String, Object>) Collections.EMPTY_MAP);
    }

    /**
     * Execute the nGql sentence.
     * Date and Datetime will be returned in UTC
     * JSON struct:
     *             {
     *               "results":[
     *                 {
     *                   "columns":[],
     *                   "data":[
     *                     {
     *                       "row":row-data,
     *                       "meta":metadata
     *                     }
     *                   ],
     *                   "latencyInUs":0,
     *                   "spaceName":"",
     *                   "planDesc ":{
     *                     "planNodeDescs":[
     *                       {
     *                         "name":"",
     *                         "id":0,
     *                         "outputVar":"",
     *                         "description":{
     *                           "key":""
     *                         },
     *                         "profiles":[
     *                           {
     *                             "rows":1,
     *                             "execDurationInUs":0,
     *                             "totalDurationInUs":0,
     *                             "otherStats":{}
     *                           }
     *                         ],
     *                         "branchInfo":{
     *                           "isDoBranch":false,
     *                           "conditionNodeId":-1
     *                         },
     *                         "dependencies":[]
     *                       }
     *                     ],
     *                     "nodeIndexMap":{},
     *                     "format":"",
     *                     "optimize_time_in_us":0
     *                   },
     *                   "comment ":"",
     *                 }
     *               ],
     *              "errors":[
     *                 {
     *                   "code": 0,
     *                   "message": ""
     *                 }
     *               ]
     *             }
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     *        parameterMap The nGql parameters
     * @return The JSON string
     */
    public synchronized String executeJsonWithParameter(String stmt,
                                                        Map<String, Object> parameterMap) throws
            IOErrorException {
        if (connection == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                    "The session was released, couldn't use again.");
        }

        Map<byte[], Value> map = new HashMap<>();
        parameterMap.entrySet().stream()
                .forEach(x -> map.put(x.getKey().getBytes(), value2Nvalue(x.getValue())));
        if (connectionIsBroken.get() && retryConnect) {
            if (retryConnect()) {
                return connection.executeJsonWithParameter(sessionID, stmt, map);
            } else {
                throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                        "All servers are broken.");
            }
        }

        try {
            return connection.executeJsonWithParameter(sessionID, stmt, map);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                connectionIsBroken.set(true);
                pool.updateServerStatus();

                if (retryConnect) {
                    if (retryConnect()) {
                        connectionIsBroken.set(false);
                        return connection.executeJsonWithParameter(sessionID, stmt, map);
                    } else {
                        connectionIsBroken.set(true);
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
            }
            throw ie;
        }
    }

    /**
     * Check current connection is ok
     *
     * @return boolean
     */
    public synchronized boolean ping() {
        if (connection == null) {
            return false;
        }
        return connection.ping();
    }

    /**
     * check current session is ok
     */
    public synchronized boolean pingSession() {
        if (connection == null) {
            return false;
        }
        return connection.ping(sessionID);
    }

    /**
     * Notifies the server that the session is no longer needed
     * and returns the connection to the pool,
     * and the connection will be reuse.
     * This function is called if the user is no longer using the session.
     */
    public synchronized void release() {
        if (connection == null) {
            return;
        }
        try {
            connection.signout(sessionID);
            pool.returnConnection(connection);
        } catch (Exception e) {
            log.warn("Release session or return object to pool failed:" + e.getMessage());
        }
        connection = null;
    }

    /**
     * Gets the service address of the current connection
     *
     * @return HostAddress the graph service address
     */
    public synchronized HostAddress getGraphHost() {
        if (connection == null) {
            return null;
        }
        return connection.getServerAddress();
    }

    /**
     * get SessionID
     */
    public long getSessionID() {
        return sessionID;
    }

    /**
     * set current connection is invalid, and get a new connection from the pool,
     * if get connection failed, return false, else return true
     *
     * @return true or false
     */
    private boolean retryConnect() {
        try {
            pool.setInvalidateConnection(connection);
            SyncConnection newConn = pool.getConnection();
            if (newConn == null) {
                log.error("Get connection object failed.");
                return false;
            }
            connection = newConn;
            return true;
        } catch (Exception e) {
            log.error("Reconnected failed: " + e);
            return false;
        }
    }

    /**
     * convert java list to nebula thrift list
     *
     * @param list java list
     * @return nebula list
     */
    private static NList list2Nlist(List<Object> list) throws UnsupportedOperationException {
        NList nlist = new NList(new ArrayList<Value>());
        for (Object item : list) {
            nlist.values.add(value2Nvalue(item));
        }
        return nlist;
    }

    /**
     * convert java map to nebula thrift map
     *
     * @param map java map
     * @return nebula map
     */
    private static NMap map2Nmap(Map<String, Object> map) throws UnsupportedOperationException {
        NMap nmap = new NMap(new HashMap<byte[], Value>());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nmap.kvs.put(entry.getKey().getBytes(), value2Nvalue(entry.getValue()));
        }
        return nmap;
    }


    /**
     * convert java value type to nebula thrift value type
     *
     * @param value java obj
     * @return nebula value
     */
    public static Value value2Nvalue(Object value) throws UnsupportedOperationException {
        Value nvalue = new Value();
        if (value == null) {
            nvalue.setNVal(NullType.__NULL__);
        } else if (value instanceof Boolean) {
            boolean bval = (Boolean) value;
            nvalue.setBVal(bval);
        } else if (value instanceof Integer) {
            int ival = (Integer) value;
            nvalue.setIVal(ival);
        } else if (value instanceof Short) {
            int ival = (Short) value;
            nvalue.setIVal(ival);
        } else if (value instanceof Byte) {
            int ival = (Byte) value;
            nvalue.setIVal(ival);
        } else if (value instanceof Long) {
            long ival = (Long) value;
            nvalue.setIVal(ival);
        } else if (value instanceof Float) {
            float fval = (Float) value;
            nvalue.setFVal(fval);
        } else if (value instanceof Double) {
            double dval = (Double) value;
            nvalue.setFVal(dval);
        } else if (value instanceof String) {
            byte[] sval = ((String) value).getBytes();
            nvalue.setSVal(sval);
        } else if (value instanceof List) {
            nvalue.setLVal(list2Nlist((List<Object>) value));
        } else if (value instanceof Map) {
            nvalue.setMVal(map2Nmap((Map<String, Object>) value));
        } else if (value instanceof Value) {
            return (Value) value;
        } else if (value instanceof Date) {
            nvalue.setDVal((Date) value);
        } else if (value instanceof Time) {
            nvalue.setTVal((Time) value);
        } else if (value instanceof Duration) {
            nvalue.setDuVal((Duration) value);
        } else if (value instanceof DateTime) {
            nvalue.setDtVal((DateTime) value);
        } else if (value instanceof Geography) {
            nvalue.setGgVal((Geography) value);
        } else {
            // unsupport other Value type, use this function carefully
            throw new UnsupportedOperationException(
                    "Only support convert boolean/float/int/string/map/list to nebula.Value but was"
                            + value.getClass().getTypeName());
        }
        return nvalue;
    }

    @Override
    public synchronized void close() {
        release();
    }
}
