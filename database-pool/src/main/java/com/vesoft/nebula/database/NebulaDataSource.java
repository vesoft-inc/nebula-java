package com.vesoft.nebula.database;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaDataSource is used for
 * @Date 2020/3/17 - 13:59
 */
public interface NebulaDataSource {

    /**
     * Gets the connection and must be released after use!
     * @return connection
     */
    NebulaConnection getConnection();

    /**
     * Release the connection
     * @param connection  connection
     */
    void release(NebulaConnection connection);

    /**
     * Current number of connections
     * @return Current number of connections
     */
    int currentPoolSize();

    /**
     * Maximum number of connections
     * @return Maximum number of connections
     */
    int maxPoolSize();

    /**
     * Number of free connections
     * @return Number of free connections
     */
    int freePoolSize();

}
