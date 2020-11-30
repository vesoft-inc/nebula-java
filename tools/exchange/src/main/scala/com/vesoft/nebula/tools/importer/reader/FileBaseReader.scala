/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.vesoft.nebula.tools.importer.config.FileBaseSourceConfigEntry
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
  * The FileBaseReader is the abstract class for HDFS file reader.
  *
  * @param session
  * @param path
  */
abstract class FileBaseReader(val session: SparkSession, val path: String) extends Reader {

  require(path.trim.nonEmpty)

  override def close(): Unit = {
    session.close()
  }
}

/**
  * The ParquetReader extend the FileBaseReader and support read parquet file from HDFS.
  *
  * @param session
  * @param parquetConfig
  */
class ParquetReader(override val session: SparkSession, parquetConfig: FileBaseSourceConfigEntry)
    extends FileBaseReader(session, parquetConfig.path) {

  override def read(): DataFrame = {
    session.read.parquet(path)
  }
}

/**
  * The ORCReader extend the FileBaseReader and support read orc file from HDFS.
  *
  * @param session
  * @param orcConfig
  */
class ORCReader(override val session: SparkSession, orcConfig: FileBaseSourceConfigEntry)
    extends FileBaseReader(session, orcConfig.path) {

  override def read(): DataFrame = {
    session.read.orc(path)
  }
}

/**
  * The JSONReader extend the FileBaseReader and support read json file from HDFS.
  *
  * @param session
  * @param jsonConfig
  */
class JSONReader(override val session: SparkSession, jsonConfig: FileBaseSourceConfigEntry)
    extends FileBaseReader(session, jsonConfig.path) {

  override def read(): DataFrame = {
    session.read.json(path)
  }
}

/**
  * The CSVReader extend the FileBaseReader and support read csv file from HDFS.
  * All types of the structure are StringType.
  *
  * @param session
  * @param csvConfig
  */
class CSVReader(override val session: SparkSession, csvConfig: FileBaseSourceConfigEntry)
    extends FileBaseReader(session, csvConfig.path) {

  override def read(): DataFrame = {
    val df = session.read
      .option("delimiter", csvConfig.separator.get)
      .option("header", csvConfig.header.get)
      .option("inferSchema", "true")
      .csv(path)
    csvConfig.csvFields.map(df.toDF(_: _*)).getOrElse(df)
  }
}

/**
  * The CustomReader extend the FileBaseReader and support read text file from HDFS.
  * Transformation is a function convert a line into Row.
  * The structure of the row should be specified.
  *
  * @param session
  * @param customConfig
  * @param transformation
  * @param structType
  */
abstract class CustomReader(override val session: SparkSession,
                            customConfig: FileBaseSourceConfigEntry,
                            transformation: String => Row,
                            filter: Row => Boolean,
                            structType: StructType)
    extends FileBaseReader(session, customConfig.path) {

  override def read(): DataFrame = {
    val encoder = RowEncoder.apply(structType)
    session.read
      .text(path)
      .filter(!_.getString(0).isEmpty)
      .map(row => transformation(row.getString(0)))(encoder)
      .filter(filter)
  }
}
