/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.example;

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
                .format("nebula")
                .option("type", Type.VERTEX.getType())
                .option("hostAndPorts", "127.0.0.1:45500")
                .option("spaceName", "nb")
                .option("label", "player")
                // if configuration "returnCols" is null or "", then return all cols
                .option("returnCols", "")
                .load();
        LOGGER.info("vertex course schema: ");
        vertexDataset.printSchema();
        vertexDataset.show();

        Dataset<Row> edgeDataset = sparkSession
                .read()
                .format("nebula")
                .option("type", Type.EDGE.getType())
                .option("hostAndPorts", "127.0.0.1:45500")
                .option("spaceName", "nb")
                .option("label", "serve")
                // if configuration "returnCols" is null or "", then return all cols
                .option("returnCols", "")
                .load();
        LOGGER.info("edge schema: ");
        edgeDataset.printSchema();
        edgeDataset.show();

        LOGGER.info("vertex count: {}, edge count: {}", vertexDataset.count(), edgeDataset.count());
    }

}
