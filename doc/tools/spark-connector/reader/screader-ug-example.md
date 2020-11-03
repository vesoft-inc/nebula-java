# 应用示例

本文以一个示例说明如何使用 Spark-connector Reader 读取 Nebula Graph 的点和边数据。

## 前提条件

使用 Spark-connector Reader 前，您需要确认以下信息：

- 您的机器上已经安装了以下软件：
  - Apache Spark&trade; 2.3.0 及以上版本
  - Scala
  - Java：1.8

- 已经成功编译 Spark-connector Reader，并已经将 `nebula-spark-1.0.1.jar` 复制到本地 Maven 库。详细信息参考 [编译 Spark-connector Reader](screader-ug-compile.md)

- 已经获取 Nebula Graph 数据库的以下信息：
  - 图空间名称和分区数量（如果创建图空间时未设置分区数量，则默认使用 100）
  - 标签和边类型的名称以及属性
  - metad 服务所在机器的 IP 地址及端口号

## 操作步骤

参考以下步骤使用 Spark-connector Reader：

1. 在 Maven 项目的 `pom.xml` 文件中加入 `nebula-spark` 依赖。

    ```xml
    <dependency>
      <groupId>com.vesoft</groupId>
      <artifactId>nebula-spark</artifactId>
      <version>1.0.1</version>
    </dependency>
    ```

2. 构建 `SparkSession` 类，这是 Spark SQL 的编码入口。

    ```
    val sparkConf = new SparkConf
    sparkConf
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array[Class[_]](classOf[TCompactProtocol]))
    val sparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .master("local")
      .getOrCreate()
    ```

    其中，关于 `.master()` 的设置，参考 [Spark 配置的 Master URLs](https://spark.apache.org/docs/latest/submitting-applications.html#master-urls "点击前往 Spark 文档中心")。

3. 按以下说明修改配置，利用 Spark 读取 Nebula Graph 的点或者边数据，得到 DataFrame。

    ```shell
    // 读取 Nebula Graph 的点数据
    val vertexDataset: Dataset[Row] =
          sparkSession.read
            .nebula("127.0.0.1:45500", "spaceName", "100")
            .loadVerticesToDF("tag", "*")
    vertexDataset.show()

    // 读取 Nebula Graph 的边数据
    val edgeDataset: Dataset[Row] =
          sparkSession.read
            .nebula("127.0.0.1:45500", "spaceName", "100")
            .loadEdgesToDF("edge", "field1,field2")
    edgeDataset.show()
    ```

    其中配置说明如下：

    - `nebula(<address: String>, <space: String>, <partitionNum: String>)`，所有参数均为必需参数。

      - `<address: String>`：配置为 Nebula Graph 数据库 metad 服务所在的服务器地址及端口，如果有多个 metad 服务复本，则配置为多个地址，以英文逗号分隔，例如 `"ip1:45500,ip2:45500"`。默认端口号为 45500。
      - `<space: String>`: 配置为 Nebula Graph 的图空间名称。
      - `<partitionNum: String>`：配置为 Nebula Graph 里创建图空间时指定的分区数量。如果您在创建 Nebula Graph 图空间时未指定分区数量，这里设置为默认分区数量 100。

    - `loadVerticesToDF(<tag: String>, <fields: String>)`，所有参数均为必需参数。

      - `<tag: String>`：配置为指定 Nebula Graph 图空间中某个标签的名称。
      - `<fields: String>`：配置为指定标签的属性名称，不允许为空。如果一个标签有多个属性，则以英文逗号分隔。如果指定了属性名称，表示只读取指定的属性。如果配置为 `*`，表示读取指定标签的所有属性。

    - `loadEdgesToDF(<edge: String>, <fields: String>)`，所有参数均为必需参数。

      - `<edge: String>`：配置为指定 Nebula Graph 图空间中某个边类型的名称。
      - `<fields: String>`：配置为指定边类型的属性名称，不允许为空。如果一个边类型有多个属性，则以英文逗号分隔。如果指定了属性名称，表示只读取指定的属性，如果配置为 `*` 表示读取指定边类型的所有属性。

以下为读取结果示例。

- 读取点数据

    ```
    20/10/27 08:51:04 INFO DAGScheduler: Job 0 finished: show at Main.scala:61, took 1.873141 s
    +---------+----------+---+
    |_vertexId|      name|age|
    +---------+----------+---+
    |        0|  Tom55322| 19|
    | 84541440|Tom4152378| 27|
    | 67829760|  Tom24006| 10|
    | 51118080|  Tom84165| 62|
    | 34406400|  Tom17308|  1|
    | 17694720|  Tom73089| 56|
    |   983040|  Tom82311| 95|
    | 68812800|  Tom61046| 93|
    | 52101120|  Tom52116| 45|
    | 18677760|   Tom4773| 18|
    |  1966080|  Tom25979| 20|
    | 69795840|  Tom92575|  9|
    | 53084160|  Tom48645| 29|
    | 36372480|  Tom20594| 86|
    | 19660800|  Tom27071| 32|
    |  2949120|    Tom630| 61|
    | 70778880|  Tom82319| 78|
    | 37355520|  Tom38207| 31|
    | 20643840|  Tom56158| 73|
    |  3932160|  Tom36933| 59|
    +---------+----------+---+
    only showing top 20 rows
    ```

- 读取边数据

    ```
    20/10/27 08:56:57 INFO DAGScheduler: Job 4 finished: show at Main.scala:71, took 0.085975 s
    +------+------+----------+--------+
    |_srcId|_dstId|start_year|end_year|
    +------+------+----------+--------+
    |   101|   201|      2002|    2020|
    |   102|   201|      2002|    2015|
    +------+------+----------+--------+
    ```
