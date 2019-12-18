/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.utils;

public class NebulaKeysUtils {
    public static final int PARTITION_ID_SIZE = 4;
    public static final int VERTEX_ID_SIZE = 8;
    public static final int TAG_ID_SIZE = 4;
    public static final int TAG_VERSION_SIZE = 8;
    public static final int EDGE_TYPE_SIZE = 4;
    public static final int EDGE_RANKING_SIZE = 8;
    public static final int EDGE_VERSION_SIZE = 8;
    public static final int VERTEX_SIZE = PARTITION_ID_SIZE + VERTEX_ID_SIZE
            + TAG_ID_SIZE + TAG_VERSION_SIZE;
    public static final int EDGE_SIZE = PARTITION_ID_SIZE + VERTEX_ID_SIZE
            + EDGE_TYPE_SIZE + EDGE_RANKING_SIZE + VERTEX_ID_SIZE + EDGE_VERSION_SIZE;

    public static final int DATA_KEY_TYPE = 0x00000001;
    public static final int TAG_MASK      = 0xBFFFFFFF;
    public static final int EDGE_MASK     = 0x40000000;
}
