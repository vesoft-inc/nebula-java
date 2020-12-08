/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import com.vesoft.nebula.tools.connector.{DataTypeEnum, NebulaOptions, NebulaUtils}
import com.vesoft.nebula.ColumnDef
import com.vesoft.nebula.utils.NebulaTypeUtil
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.{DataType, DataTypes, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

private case class NebulaRelation(override val sqlContext: SQLContext, nebulaOptions: NebulaOptions)
    extends BaseRelation
    with TableScan {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  private val labelFields: Map[String, List[StructField]] = Map()

  private var datasetSchema: StructType = _

  override val needConversion: Boolean = false

  override def schema: StructType = getSchema(nebulaOptions)

  /**
    * convert [[ColumnDef]] to spark [[DataType]]
    */
  private def columnDef2dataType(columnDef: ColumnDef): DataType = {
    NebulaUtils.convertDataType(NebulaTypeUtil.supportedTypeToClass(columnDef.getType.getType))
  }

  /**
    * return the dataset's schema. Schema includes configured cols in returnCols or includes all properties in nebula.
    */
  def getSchema(nebulaOptions: NebulaOptions): StructType = {
    val returnColMap                    = nebulaOptions.getReturnColMap
    val fields: ListBuffer[StructField] = new ListBuffer[StructField]
    val metaClient                      = NebulaUtils.createMetaClient(nebulaOptions.getHostAndPorts, nebulaOptions)

    import scala.collection.JavaConverters._
    var schemaCols: Seq[ColumnDef] = Seq()

    returnColMap.keySet.foreach(k => {
      val schema = if (DataTypeEnum.VERTEX.toString.equalsIgnoreCase(nebulaOptions.dataType)) {
        fields.append(DataTypes.createStructField("_vertexId", DataTypes.StringType, false))
        metaClient.getTag(nebulaOptions.spaceName, nebulaOptions.label)
      } else {
        fields.append(DataTypes.createStructField("_srcId", DataTypes.StringType, false))
        fields.append(DataTypes.createStructField("_dstId", DataTypes.StringType, false))
        metaClient.getEdge(nebulaOptions.spaceName, nebulaOptions.label)
      }
      schemaCols = schema.columns.asScala
      if (nebulaOptions.allCols) {
        // if allCols is true, then fields should contain all properties.
        schemaCols
          .foreach(columnDef => {
            LOG.info(s"prop name ${columnDef.getName}, type ${columnDef.getType}")
            fields.append(DataTypes.createStructField(columnDef.getName, columnDef2dataType(columnDef), true))
          })
      } else {
        // create map, columnName -> columnDef
        val colName2Def = schemaCols.groupBy(_.getName).mapValues(_.last)
        returnColMap(k)
          .foreach(returnCol => {
            colName2Def.get(returnCol) match {
              case Some(columnDef) =>
                fields.append(DataTypes.createStructField(returnCol, columnDef2dataType(columnDef), true))
              case None => LOG.warn(s"label ${nebulaOptions.label} doesn't contain col $returnCol")
            }
          })
      }
      labelFields ++ Map(k -> fields)
      datasetSchema = new StructType(fields.toArray)
    })
    LOG.info(s"dataset's schema: $datasetSchema")
    datasetSchema
  }

  override def buildScan(): RDD[Row] = {
    new NebulaRDD(sqlContext, nebulaOptions, datasetSchema).asInstanceOf[RDD[Row]]
  }
}
