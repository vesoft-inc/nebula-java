package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.common.Type;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private ConnectInfo connectInfo;
    private ScanInfo scanInfo;
    private Map<String, Integer> propIndexMap;

    public NebulaRelation(SQLContext sqlContext, ConnectInfo connectInfo, ScanInfo scanInfo) {
        this.sqlContext = sqlContext;
        this.connectInfo = connectInfo;
        this.scanInfo = scanInfo;

        initParameters(scanInfo);
    }

    private void initParameters(ScanInfo scanInfo) {

        Set<String> returnPropSet = new HashSet<>();
        Map<String, List<String>> returnColMap = scanInfo.getReturnColMap();
        LOGGER.info("return col map: {}", returnColMap);
        for (Map.Entry<String, List<String>> returnColEntry : returnColMap.entrySet()) {
            List<String> returnCols = returnColEntry.getValue();
            for (String returnCol : returnCols) {
                returnPropSet.add(returnCol);
            }
        }
        LOGGER.info("return prop set: {}", returnPropSet);

        List<StructField> fields = new ArrayList<>();
        if (Type.VERTEX.getType().equalsIgnoreCase(scanInfo.getScanType())) {
            fields.add(DataTypes.createStructField("id", DataTypes.StringType, false));
        } else {
            fields.add(DataTypes.createStructField("src", DataTypes.StringType, false));
            fields.add(DataTypes.createStructField("dst", DataTypes.StringType, false));
        }
        /*
            spark read result has only one dataset, but scan result contain difference properties,
            so create a schema contain all properties
         */
        for (String returnCol : returnPropSet) {
            fields.add(DataTypes.createStructField(returnCol, DataTypes.StringType, true));
        }
        schema = DataTypes.createStructType(fields);

        propIndexMap = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            propIndexMap.put(fields.get(i).name(), i);
        }
        LOGGER.info("propIndexMap: {}", propIndexMap);
    }

    public SQLContext sqlContext() {
        return sqlContext;
    }

    public StructType schema() {
        return schema;
    }

    public RDD<Row> buildScan() {
        return new NebulaRDD(sqlContext, scanInfo, connectInfo, propIndexMap);
    }
}
