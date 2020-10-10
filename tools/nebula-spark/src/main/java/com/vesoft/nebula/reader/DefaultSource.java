/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.google.common.base.Preconditions;
import com.vesoft.nebula.bean.Parameters;
import com.vesoft.nebula.bean.DataSourceConfig;
import com.vesoft.nebula.common.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.RelationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Map;

import java.util.regex.Pattern;

public class DefaultSource implements RelationProvider, DataSourceRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSource.class);
    private static final String RETURN_COL_REGEX = "(\\w+)(,\\w+)*";

    private DataSourceConfig dataSourceConfig;

    @Override
    public String shortName() {
        return "nebula";
    }

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, Map<String, String> parameters) {
        // check and parse parameter spaceName
        Preconditions.checkArgument(parameters.get(Parameters.SPACE_NAME).isDefined(), "spaceName is not configured.");
        String spaceName = parameters.get(Parameters.SPACE_NAME).get();

        // check and parse parameter type
        Preconditions.checkArgument(parameters.get(Parameters.TYPE).isDefined(), "type is not configured.");

        String type = parameters.get(Parameters.TYPE).get();
        Preconditions.checkArgument(type.equalsIgnoreCase(Type.EDGE.getType())
                        || type.equalsIgnoreCase(Type.VERTEX.getType()),
                "type '%s' is illegal, it should be '%s' or '%s'",
                type, Type.VERTEX.getType(), Type.EDGE.getType());

        // check and parse parameter label
        Preconditions.checkArgument(parameters.get(Parameters.LABEL).isDefined(), "label is not configured.");
        String label = parameters.get(Parameters.LABEL).get();

        // check and parse parameter hostAndPorts
        Preconditions.checkArgument(parameters.get(Parameters.HOST_AND_PORTS).isDefined(), "hostAndPorts is not configured.");
        String hostAndPorts = parameters.get(Parameters.HOST_AND_PORTS).get();

        // check and parse parameter returnCols
        Preconditions.checkArgument(parameters.get(Parameters.RETURN_COLS).isDefined(), "returnCols is not configured.");
        String returnCols = parameters.get(Parameters.RETURN_COLS).get();
        boolean isReturnColLegal = StringUtils.isBlank(returnCols) || Pattern.matches(RETURN_COL_REGEX, returnCols);
        Preconditions.checkArgument(isReturnColLegal,
                "returnCols '%s' is illegal, the pattern should be blank or string like a,b",
                returnCols);

        // check and parse parameter partitionNumber
        Preconditions.checkArgument(parameters.get(Parameters.PARTITION_NUMBER).isDefined(), "partition is not configured.");
        String partitionNumber = parameters.get(Parameters.PARTITION_NUMBER).get();

        dataSourceConfig = new DataSourceConfig(spaceName, type,
                label, returnCols, Integer.parseInt(partitionNumber), hostAndPorts);
        LOGGER.info("dataSourceConfig: {}", dataSourceConfig);

        return new NebulaRelation(sqlContext, dataSourceConfig);
    }
}
