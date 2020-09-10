/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.google.common.base.Preconditions;
import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.Parameters;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.common.Type;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.RelationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Map;
import scala.runtime.AbstractFunction0;

import java.util.regex.Pattern;

public class NebulaDataSource implements RelationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaDataSource.class);
    private static final String RETURN_COL_REGEX = "(\\w+)(,\\w+)*";


    private ConnectInfo connectInfo;
    private ScanInfo scanInfo;

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, Map<String, String> parameters) {
        // check and parse parameter spaceName
        Preconditions.checkArgument(parameters.get(Parameters.SPACE_NAME).isDefined(), "spaceName is not configured.");
        String spaceName = parameters.get("spaceName").get();

        // check and parse parameter scanType
        Preconditions.checkArgument(parameters.get(Parameters.SCAN_TYPE).isDefined(), "scanType is not configured.");

        String scanType = parameters.get(Parameters.SCAN_TYPE).get();
        Preconditions.checkArgument(scanType.equalsIgnoreCase(Type.EDGE.getType())
                        || scanType.equalsIgnoreCase(Type.VERTEX.getType()),
                "scan_type '%s' is illegal, it should be '%s' or '%s'",
               scanType , Type.VERTEX.getType(), Type.EDGE.getType());

        // check and parse parameter label
        Preconditions.checkArgument(parameters.get(Parameters.LABEL).isDefined(), "label is not configured.");
        String label = parameters.get(Parameters.LABEL).get();

        // check and parse parameter hostAndPorts
        Preconditions.checkArgument(parameters.get(Parameters.HOST_AND_PORTS).isDefined(), "hostAndPorts is not configured.");
        String hostAndPorts = parameters.get(Parameters.HOST_AND_PORTS).get();

        // check and parse parameter returnCols
        Preconditions.checkArgument(parameters.get(Parameters.RETURN_COLS).isDefined(), "returnCols is not configured.");
        String returnCols = parameters.get(Parameters.RETURN_COLS).get();
        boolean isReturnColLegal = Pattern.matches(RETURN_COL_REGEX, returnCols);
        Preconditions.checkArgument(isReturnColLegal,
                "returnCols '%s' is illegal, the pattern should like a,b",
                returnCols);

        // check and parse parameter partitionNumber
        String partitionNumber = parameters.getOrElse(Parameters.PARTITION_NUMBER,
                new AbstractFunction0<String>() {
                    @Override
                    public String apply() {
                        return String.valueOf(Runtime.getRuntime().availableProcessors());
                    }
                });


        connectInfo = new ConnectInfo(spaceName, hostAndPorts);
        LOGGER.info("connectInfo, {}", connectInfo);

        scanInfo = new ScanInfo(spaceName,scanType,
                label, returnCols, Integer.parseInt(partitionNumber));
        LOGGER.info("scanInfo: {}", scanInfo);

        return new NebulaRelation(sqlContext, connectInfo, scanInfo);
    }
}
