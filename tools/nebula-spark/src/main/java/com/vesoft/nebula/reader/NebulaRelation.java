/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.facebook.thrift.TException;
import com.vesoft.nebula.bean.DataSourceConfig;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.common.Type;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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

    private DataSourceConfig dataSourceConfig;

    public NebulaRelation(SQLContext sqlContext, DataSourceConfig dataSourceConfig) {
        this.sqlContext = sqlContext;
        this.dataSourceConfig = dataSourceConfig;

        initSchema(dataSourceConfig);
    }

    /**
     * init result dataset's schema
     *
     * @param dataSourceConfig
     */
    private void initSchema(DataSourceConfig dataSourceConfig) {
        Map<String, List<String>> returnColMap = dataSourceConfig.getReturnColMap();
        LOGGER.info("return col map: {}", returnColMap);

        List<StructField> fields = new ArrayList<>();

        MetaClientImpl metaClient = new MetaClientImpl(dataSourceConfig.getHostAndPorts());
        try {
            metaClient.connect();
        } catch (TException e) {
            throw new GraphConnectException(e.getMessage(), e);
        }

        Map<String, Class> schemaColAndType;
        for (Map.Entry<String, List<String>> returnColEntry : returnColMap.entrySet()) {
            if (Type.VERTEX.getType().equalsIgnoreCase(dataSourceConfig.getType())) {
                fields.add(DataTypes.createStructField("_vertexId", DataTypes.StringType, false));
                schemaColAndType = metaClient.getTagSchema(dataSourceConfig.getNameSpace(), dataSourceConfig.getLabel());
            } else {
                fields.add(DataTypes.createStructField("_srcId", DataTypes.StringType, false));
                fields.add(DataTypes.createStructField("_dstId", DataTypes.StringType, false));
                schemaColAndType = metaClient.getEdgeSchema(dataSourceConfig.getNameSpace(), dataSourceConfig.getLabel());
            }

            if (dataSourceConfig.getAllCols()) {
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
                        LOGGER.warn("label {} doesn't contain col {}", dataSourceConfig.getLabel(), returnCol);
                    }
                }
            }

            labelFields.put(returnColEntry.getKey(), fields);
            schema = new StructType(fields.toArray(new StructField[fields.size()]));
            LOGGER.info("return prop set for label {} : {}", returnColEntry.getKey(), fields);
        }
    }

    @Override
    public SQLContext sqlContext() {
        return sqlContext;
    }

    @Override
    public StructType schema() {
        return schema;
    }

    @Override
    public RDD<Row> buildScan() {
        return new NebulaRDD(sqlContext, dataSourceConfig);
    }
}
