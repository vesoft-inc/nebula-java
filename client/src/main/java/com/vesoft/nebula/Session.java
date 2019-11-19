/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

/**
 *
 */
public interface Session extends AutoCloseable {

    /**
     *
     */
    public void connect();

    /**
     * @param sentence
     * @return
     */
    public int execute(String sentence);

    /**
     * @param sentence
     * @return
     */
    public int executeQuery(String sentence);
}
