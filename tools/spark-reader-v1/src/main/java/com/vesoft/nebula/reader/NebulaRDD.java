/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.common.Type;
import java.util.Map;
import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.collection.Iterator;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassManifestFactory$;
import scala.reflect.ClassTag;

public class NebulaRDD extends RDD<Row> {

    private static final ClassTag<Row> ROW_TAG = ClassManifestFactory$.MODULE$.fromClass(Row.class);

    private ConnectInfo connectInfo;
    private ScanInfo scanInfo;
    private Map<String, Integer> propIndexMap;

    /**
     * @param sqlContext    sqlContext
     * @param scanInfo      scan info
     * @param connectInfo   nebula connect info
     * @param propIndexMap  label and its properties in schema index map
     */
    public NebulaRDD(SQLContext sqlContext, ScanInfo scanInfo,
                     ConnectInfo connectInfo, Map<String, Integer> propIndexMap) {
        super(sqlContext.sparkContext(), new ArrayBuffer<>(), ROW_TAG);

        this.propIndexMap = propIndexMap;
        this.scanInfo = scanInfo;
        this.connectInfo = connectInfo;
    }

    @Override
    public Iterator<Row> compute(Partition split, TaskContext context) {
        String scanType = scanInfo.getScanType();
        if (Type.VERTEX.getType().equalsIgnoreCase(scanType)) {
            return new ScanVertexIterator(connectInfo, split, scanInfo, propIndexMap);
        } else {
            return new ScanEdgeIterator(connectInfo, split, scanInfo, propIndexMap);
        }
    }

    @Override
    public Partition[] getPartitions() {
        int partitionNumber = scanInfo.getPartitionNumber();
        Partition[] partitions = new Partition[partitionNumber];
        for (int i = 0; i < partitionNumber; i++) {
            Partition partition = new NebulaPartition(i, partitionNumber);
            partitions[i] = partition;
        }
        return partitions;
    }
}
