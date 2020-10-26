# 应用示例

本文以一个示例说明如何使用 Spark-connector Reader 读取 Nebula Graph 的点和边数据。

## 前提条件

使用 Spark-connector Reader 前，您需要确认以下信息：

- 已经成功编译 Spark-connector Reader，并已经将 `nebula-spark-1.0.1.jar` 复制到本地 Maven 库。
- 您已经获取 Nebula Graph 数据库的以下信息：
  - 图空间名称和分区数量（如果创建图空间时未设置，则默认使用 100）
  - 标签和边类型的名称以及属性
  - Graph 服务所在机器 IP 地址及端口号

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

2. 构建 `SparkSession` 类，这是 Spark SQL 的编码入口。<!--下面需要添加 import 这两行内容吗？-->

    ```
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}


    val sparkConf = new SparkConf
    sparkConf
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array[Class[_]](classOf[TCompactProtocol]))
    val spark = SparkSession
      .builder()
      .config(sparkConf)
      .master("local")
      .getOrCreate()
    ```

    其中，关于 `.master()` 的设置，参考 [Spark 配置的 Master URLs](https://spark.apache.org/docs/latest/submitting-applications.html#master-urls "点击前往 Spark 文档中心")

3. Spark SQL 从 Spark 程序中读取 Nebula Graph 的点或者边数据得到 DataFrame。

    ```shell
    // 读取 Nebula Graph 的点数据
    val vertexDataset: Dataset[Row] =
          sparkSession.read
            .nebula("127.0.0.1:45500", "spaceName", "100")
            .loadVerticesToDF("tag", List("field1,field2"))
    vertexDataset.show()

    // 读取 Nebula Graph 的边数据
    val edgeDataset: Dataset[Row] =
          sparkSession.read
            .nebula("127.0.0.1:45500", "spaceName", "100")
            .loadEdgesToDF("edge", List("field1,field2"))
    edgeDataset.show()
    ```

其中配置说明如下：

- `nebula(<address: String>, <space: String>, <partitionNum: String>)`，所有参数均为必需参数。

  - `<address: String>`：配置为 Nebula Graph 数据库 Graph 服务所在的服务器地址及端口，如果有多个 Graph 服务复本，则配置为多个地址，以英文逗号分隔，例如 `"ip1:45500,ip2:45500"`。
  - `<space: String>`: 配置为 Nebula Graph 的图空间名称。
  - `<partitionNum: String>`：配置为 Nebula Graph 里创建图空间时指定的分区数量。如果您在创建 Nebula Graph 图空间时未指定分区数量，这里设置为默认分区数量 100。

- `loadVertices(<tag: String>, <fields: String>)`，所有参数均为必需参数。

  - `<tag: String>`：配置为指定 Nebula Graph 图空间中某个标签的名称。
  - `<fields: String>`：配置为指定标签的属性名称，不允许为空。如果一个标签有多个属性，则以英文逗号分隔。如果指定了属性名称，表示只读取指定的属性。如果配置为 `*`，表示读取指定标签的所有属性。

- `loadEdges(<edge: String>, <fields: String>)`，所有参数均为必需参数。

  - `<edge: String>`：配置为指定 Nebula Graph 图空间中某个边类型的名称。
  - `<fields: String>`：配置为指定边类型的属性名称，不允许为空。如果一个边类型有多个属性，则以英文逗号分隔。如果指定了属性名称，表示只读取指定的属性，如果配置为 `*` 表示读取指定边类型的所有属性。

读取出来就是 DataFrame。<!--给个读取出来的数据示例？-->
