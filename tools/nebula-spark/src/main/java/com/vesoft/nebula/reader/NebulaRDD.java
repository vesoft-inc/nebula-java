/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.common.Type;

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

    private ScanInfo scanInfo;

    /**
     * @param sqlContext    sqlContext
     * @param scanInfo      scan info
     */
    public NebulaRDD(SQLContext sqlContext, ScanInfo scanInfo) {
        super(sqlContext.sparkContext(), new ArrayBuffer<>(), ROW_TAG);

        this.scanInfo = scanInfo;
    }

    /**
     * start to scan vertex or edge data
     *
     * @param split
     * @param context
     *
     * @return Iterator<Row>
     * */
    @Override
    public Iterator<Row> compute(Partition split, TaskContext context) {
        String type = scanInfo.getType();
        if (Type.VERTEX.getType().equalsIgnoreCase(type)) {
            return new ScanVertexIterator(split, scanInfo);
        } else {
            return new ScanEdgeIterator(split, scanInfo);
        }
    }

    @Override
    public Partition[] getPartitions() {
        int partitionNumber = scanInfo.getPartitionNumber();
        Partition[] partitions = new Partition[partitionNumber];
        for (int i = 0; i < partitionNumber; i++) {
            Partition partition = new NebulaPartition(i);
            partitions[i] = partition;
        }
        return partitions;
    }
}
