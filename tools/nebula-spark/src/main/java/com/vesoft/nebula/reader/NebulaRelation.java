/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.common.Type;

import java.io.Serializable;
import java.util.*;

import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.TableScan;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaRelation extends BaseRelation implements Serializable, TableScan {

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaRelation.class);

    private SQLContext sqlContext;
    private StructType schema;
    private final Map<String, List<StructField>> labelFields = new HashMap<>();

    private ConnectInfo connectInfo;
    private ScanInfo scanInfo;

    public NebulaRelation(SQLContext sqlContext, ConnectInfo connectInfo, ScanInfo scanInfo) {
        this.sqlContext = sqlContext;
        this.connectInfo = connectInfo;
        this.scanInfo = scanInfo;

        initSchema(scanInfo);
    }

    /**
     * init result dataset's schema
     *
     * @param scanInfo
     * */
    private void initSchema(ScanInfo scanInfo) {
        Map<String, List<String>> returnColMap = scanInfo.getReturnColMap();
        LOGGER.info("return col map: {}", returnColMap);

        List<StructField> fields = new ArrayList<>();
        for (Map.Entry<String, List<String>> returnColEntry : returnColMap.entrySet()) {
            if (Type.VERTEX.getType().equalsIgnoreCase(scanInfo.getScanType())) {
                fields.add(DataTypes.createStructField("_vertexId", DataTypes.StringType, false));
            } else {
                fields.add(DataTypes.createStructField("_srcId", DataTypes.StringType, false));
                fields.add(DataTypes.createStructField("_dstId", DataTypes.StringType, false));
            }

            for (String returnCol : returnColEntry.getValue()) {
                fields.add(DataTypes.createStructField(returnCol, DataTypes.StringType, true));
            }
            // TODO if allCols is true, then fields should contain all properties.

            labelFields.put(returnColEntry.getKey(), fields);
            schema = new StructType(fields.toArray(new StructField[fields.size()]));
            LOGGER.info("return prop set for label {} : {}", returnColEntry.getKey(), fields);
        }
    }

    public SQLContext sqlContext() {
        return sqlContext;
    }

    public StructType schema() {
        return schema;
    }

    public RDD<Row> buildScan() {
        return new NebulaRDD(sqlContext, scanInfo, connectInfo);
    }
}
