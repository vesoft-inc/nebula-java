/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.example;

import com.facebook.thrift.protocol.TCompactProtocol;
import com.vesoft.nebula.bean.Parameters;
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
                .option(Parameters.TYPE, Type.VERTEX.getType())
                .option(Parameters.HOST_AND_PORTS, "127.0.0.1:45500")
                .option(Parameters.PARTITION_NUMBER, "100")
                .option(Parameters.SPACE_NAME, "nb")
                .option(Parameters.LABEL, "player")
                // if configuration "returnCols" is null or "", then return all cols
                .option(Parameters.RETURN_COLS, "")
                .load();
        LOGGER.info("vertex course schema: ");
        vertexDataset.printSchema();
        vertexDataset.show();

        Dataset<Row> edgeDataset = sparkSession
                .read()
                .format("nebula")
                .option(Parameters.TYPE, Type.EDGE.getType())
                .option(Parameters.HOST_AND_PORTS, "127.0.0.1:45500")
                .option(Parameters.PARTITION_NUMBER, "100")
                .option(Parameters.SPACE_NAME, "nb")
                .option(Parameters.LABEL, "serve")
                // if configuration "returnCols" is null or "", then return all cols
                .option(Parameters.RETURN_COLS, "")
                .load();
        LOGGER.info("edge schema: ");
        edgeDataset.printSchema();
        edgeDataset.show();

        LOGGER.info("vertex count: {}, edge count: {}", vertexDataset.count(), edgeDataset.count());
    }

}
