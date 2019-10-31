/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools;

public class Constant {

    public static final String EMPTY = "";

    public static final int DEFAULT_INSERT_BATCH_SIZE = 16;
    public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 3 * 1000;
    public static final int DEFAULT_CONNECTION_RETRY = 3;
    public static final int DEFAULT_EXECUTION_RETRY = 3;
    public static final String DEFAULT_ERROR_PATH = "/tmp/error";
    public static final String DEFAULT_ADDRESSES = "127.0.0.1:3699";

    public static final String USE_TEMPLATE = "USE %s";
    public static final String INSERT_VERTEX_VALUE_TEMPLATE = "%d: (%s)";
    public static final String INSERT_EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%d->%d: (%s)";
    public static final String INSERT_EDGE_VALUE_TEMPLATE = "%d->%d@%d: (%s)";
    public static final String BATCH_INSERT_TEMPLATE = "INSERT %s %s(%s) values %s";

    public static final String VERTEX = "vertex";
    public static final String EDGE = "edge";

    /**
     * The index of id in csv file when import vertex.
     * Form of data of a single line in csv would like:
     *  id, prop1, prop2, ...
     */
    public static final Integer ID_INDEX = 0;

    /**
     * The index of src/dst/ranking in csv file when import edge.
     * Form of data of a single line in csv would like:
     *  src, dst, [ranking], prop1, prop2, ...
     */
    public static final Integer SRC_INDEX = 0;
    public static final Integer DST_INDEX = 1;
    public static final Integer RANKING_INDEX = 2;

    /**
     * The index of lst/lng/poi_id in csv file when import geo.
     * Form of data of a single line in csv would like:
     *  lat, lng, poi_id
     */
    public static final Integer LAT_INDEX = 0;
    public static final Integer LNG_INDEX = 1;
    public static final Integer POI_ID_INDEX = 2;

    public static Integer maxCellCoverNums = 18;
    public static Integer minCellLevel = 5;
    public static Integer maxCellLevel = 24;

}
