/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.RelationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Map;
import scala.runtime.AbstractFunction0;

public class NebulaDataSource implements RelationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaDataSource.class);

    private ConnectInfo connectInfo;
    private ScanInfo scanInfo;

    @Override
    public BaseRelation createRelation(SQLContext sqlContext, Map<String, String> parameters) {
        // parse and check parameters
        String spaceName = parameters.get("spaceName").get();
        String hostAndPorts = parameters.get("hostAndPorts").get();
        connectInfo = new ConnectInfo(spaceName, hostAndPorts);
        LOGGER.info("connectInfo, {}", connectInfo);

        String partitionNumber = parameters.getOrElse("partitionNumber",
                new AbstractFunction0<String>() {
                    @Override
                    public String apply() {
                        return String.valueOf(Runtime.getRuntime().availableProcessors());
                    }
                });
        scanInfo = new ScanInfo(parameters.get("importType").get(),
                parameters.get("returnCols").get(), Integer.valueOf(partitionNumber));
        LOGGER.info("scanInfo: {}", scanInfo);

        return new NebulaRelation(sqlContext, connectInfo, scanInfo);
    }
}
