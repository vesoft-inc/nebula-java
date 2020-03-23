/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description ConnectionManager
 * @Date 2020/3/17 - 15:13
 */
public interface ConnectionManager {

    /**
     * Maximum number of connections
     * @return Maximum number of connections
     */
    int maxConnectionCount();

    /**
     * Current number of connections
     * @return Current number of connections
     */
    int currentConnectionCount();

    /**
     * Active connection number
     * @return Active connection number
     */
    int activeConnectionCount();

    /**
     * Number of free connections
     * @return Number of free connections
     */
    int freeConnectionCount();

    /**
     * Gets an available idle connection
     * @return ree connection
     */
    NebulaConnection getConnection();

    /**
     * Can you supplement the connection
     * @return
     */
    boolean canAddConnection();

    /**
     * Supplementary database connection
     * @param connection Supplementary database connection
     */
    boolean addConnection(NebulaConnection connection);

    /**
     *  Release a connection
     * @param connection Release a connection
     */
    void releaseConnection(NebulaConnection connection);


}
