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

    public static Integer maxCellCoverNums = 18;
    public static Integer minCellLevel = 5;
    public static Integer maxCellLevel = 24;

}
