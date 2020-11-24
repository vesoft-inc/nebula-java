/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.utils;

public class NebulaConstant {
    // template for insert statement
    public static String BATCH_INSERT_TEMPLATE = "INSERT %s %s(%s) VALUES %s";
    public static String VERTEX_VALUE_TEMPLATE = "%s: (%s)";
    public static String VERTEX_VALUE_TEMPLATE_WITH_POLICY = "%s(\"%s\"): (%s)";
    public static String ENDPOINT_TEMPLATE = "%s(\"%s\")";
    public static String EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%s->%s: (%s)";
    public static String EDGE_VALUE_TEMPLATE = "%s->%s@%d: (%s)";

    // Delimiter
    public static String COMMA = ",";
    public static String SUB_LINE = "_";
    public static String POINT = ".";
    public static String SPLIT_POINT = "\\.";


    // default value for read & write
    public static final int DEFAULT_SCAN_LIMIT = 2000;
    public static final int DEFAULT_WRITE_BATCH = 2000;
    public static final int DEFAULT_ROW_INFO_INDEX = -1;

    // default value for connection
    public static final int DEFAULT_TIMEOUT_MS = 1000;
    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 3000;
    public static final int DEFAULT_CONNECT_RETRY = 3;
    public static final int DEFAULT_EXECUTION_RETRY = 3;

}
