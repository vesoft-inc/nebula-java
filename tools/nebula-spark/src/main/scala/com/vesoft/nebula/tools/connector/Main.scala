package com.vesoft.nebula.tools.connector

import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()

    val session = spark.master("local").getOrCreate()
    val df      = session.read.nebulaVertices("test", "tag", List("column"), "127.0.0.1:8989")

    df.show(10)
    df.write.writeVertices("test", "tag")
  }

}
