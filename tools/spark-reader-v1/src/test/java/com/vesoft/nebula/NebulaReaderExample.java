/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import com.facebook.thrift.protocol.TCompactProtocol;
import com.vesoft.nebula.common.Type;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaReaderExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaReaderExample.class);

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.registerKryoClasses(new Class[]{TCompactProtocol.class});
        SparkSession sparkSession = SparkSession
                .builder()
                .config(sparkConf)
                .master("local[4]")
                .getOrCreate();

        Dataset<Row> vertexDataset = sparkSession
                .read()
                .format("com.vesoft.nebula.reader.NebulaDataSource")
                .option("importType", Type.VERTEX.getType())
                .option("ip", "127.0.0.1")
                .option("spaceName", "test")
                .option("storagePort", "45500")
                .option("returnCols", "course=name;building=name;student=name")
                .load();
        LOGGER.info("vertex schema: ");
        vertexDataset.printSchema();
        vertexDataset.show();

        Dataset<Row> edgeDataset = sparkSession
                .read()
                .format("com.vesoft.nebula.reader.NebulaDataSource")
                .option("importType", Type.EDGE.getType())
                .option("ip", "127.0.0.1")
                .option("spaceName", "test")
                .option("storagePort", "45500")
                .option("returnCols", "like=likeness;select=grade")
                .load();
        LOGGER.info("edge schema: ");
        edgeDataset.printSchema();
        edgeDataset.show();

        LOGGER.info("vertex count: {}, edge count: {}", vertexDataset.count(), edgeDataset.count());

    }

}
