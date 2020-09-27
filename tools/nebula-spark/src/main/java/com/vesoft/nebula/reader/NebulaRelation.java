/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.facebook.thrift.TException;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.common.Type;

import java.io.Serializable;
import java.util.*;

import com.vesoft.nebula.exception.GraphConnectException;
import com.vesoft.nebula.util.DataTypeConverter;
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

    private ScanInfo scanInfo;

    public NebulaRelation(SQLContext sqlContext, ScanInfo scanInfo) {
        this.sqlContext = sqlContext;
        this.scanInfo = scanInfo;

        initSchema(scanInfo);
    }

    /**
     * init result dataset's schema
     *
     * @param scanInfo
     */
    private void initSchema(ScanInfo scanInfo) {
        Map<String, List<String>> returnColMap = scanInfo.getReturnColMap();
        LOGGER.info("return col map: {}", returnColMap);

        List<StructField> fields = new ArrayList<>();

        MetaClientImpl metaClient = new MetaClientImpl(scanInfo.getHostAndPorts());
        try {
            metaClient.connect();
        } catch (TException e) {
            throw new GraphConnectException(e.getMessage(), e);
        }

        Map<String, Class> schemaColAndType;
        for (Map.Entry<String, List<String>> returnColEntry : returnColMap.entrySet()) {
            if (Type.VERTEX.getType().equalsIgnoreCase(scanInfo.getType())) {
                fields.add(DataTypes.createStructField("_vertexId", DataTypes.StringType, false));
                schemaColAndType = metaClient.getTagSchema(scanInfo.getNameSpace(), scanInfo.getLabel());
            } else {
                fields.add(DataTypes.createStructField("_srcId", DataTypes.StringType, false));
                fields.add(DataTypes.createStructField("_dstId", DataTypes.StringType, false));
                schemaColAndType = metaClient.getEdgeSchema(scanInfo.getNameSpace(), scanInfo.getLabel());
            }

            if (scanInfo.getAllCols()) {
                // if allCols is true, then fields should contain all properties.
                for (Map.Entry<String, Class> colTypeEntry : schemaColAndType.entrySet()) {
                    fields.add(DataTypes.createStructField(colTypeEntry.getKey(),
                            DataTypeConverter.convertDataType(colTypeEntry.getValue()),
                            true));
                }
            } else {
                for (String returnCol : returnColEntry.getValue()) {
                    if (schemaColAndType.containsKey(returnCol)) {
                        fields.add(DataTypes.createStructField(returnCol,
                                DataTypeConverter.convertDataType(schemaColAndType.get(returnCol)),
                                true));
                    } else {
                        LOGGER.warn("label {} doesn't contain col {}", scanInfo.getLabel(), returnCol);
                    }
                }
            }

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
        return new NebulaRDD(sqlContext, scanInfo);
    }
}
