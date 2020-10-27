# 什么是 Spark-connector Reader

Spark-connector Reader 是 Spark-connector 的组成部分，为您提供了 Spark SQL 接口，您可以使用 Spark SQL 接口编程读取 Nebula Graph 图数据，单次读取一个标签或边类型的数据，并将读取的结果组装成 Spark 的 DataFrame。

## Spark-connector Reader 实现原理

Spark SQL 是 Spark 中用于处理结构化数据的一个编程模块。它提供了一个称为 DataFrame 的编程抽象，并且可以充当分布式 SQL 查询引擎。Spark SQL 允许用户自定义数据源，支持对外部数据源进行扩展。通过 Spark SQL 读取到的数据格式是以命名列方式组织的分布式数据集 DataFrame，而且Spark SQL 提供了众多 API 方便用户对 DataFrame 进行计算和转换，能对多种数据源使用 DataFrame 接口。

### 接口

Spark 使用 `org.apache.spark.sql` 调用外部数据源包。以下为 Spark SQL 提供的扩展数据源相关的接口。

- 基本接口，包括：

  - `BaseRelation`: 表示具有已知 Schema 的元组的集合。所有继承了 `BaseRelation` 的子类都必须生成 `StructType` 格式的 Schema。换句话说，`BaseRelation` 定义了从数据源中读取的数据在 Spark SQL 的 DataFrame 中存储的数据格式。

  - `RelationProvider`: 获取参数列表，根据给定的参数返回一个新的 `BaseRelation`。

  - `DataSourceRegister`: “注册数据源”的简称，在使用数据源时不用写数据源的全限定类名，而只需要写自定义的 `shortName` 即可。

- Providers 接口，包括：

  - `RelationProvider`：从指定数据源中生成自定义的 `relation`。`RelationProvider#createRelation` 会基于给定的参数生成新的 `relation`。

  - `SchemaRelationProvider`：可以基于给定的参数和给定的 Schema 信息生成新的 `relation`。

- RDD 接口，包括：

  - `RDD[InternalRow]`: 从数据源中扫描出来后，需要构造成 `RDD[Row]`。

Spark-connector Reader 根据 Nebula Graph 的数据源自定义了部分上述部分方法，从而实现自定义 Spark 外部数据源。

### 实现类图

在 Spark-connector Reader 中，Nebula Graph 作为 Spark SQL 的外部数据源，通过 `sparkSession.read` 的形式读取数据。该功能实现的类图展示如下图所示。

![Spark-connector Reader 实现类图](https://docs-cdn.nebula-graph.com.cn/nebula-java-tools-docs/sc-ug-001.png "Spark-connector Reader 实现类图")

处理流程如下：

1. 定义数据源 `NebulaRelationProvider`：继承 `RelationProvider` 自定义 `relation`，继承 `DataSourceRegister` 注册外部数据源。

2. 定义 `NebulaRelation`，实现 Nebula Graph 图数据 Schema 的获取和数据转换方法。在 `NebulaRelation#getSchema` 方法中连接 Nebula Graph 的 Meta 服务获取配置的返回字段对应的 Schema 信息。

3. 定义 `NebulaRDD` 读取 Nebula Graph 图数据。其中，`NebulaRDD#compute` 方法定义了如何读取 Nebula Graph 图数据，主要涉及到扫描 Nebula Graph 图数据、将读到的 Nebula Graph 的行（Row）数据转换为 Spark 的 `InternalRow` 数据，以 `InternalRow` 组成 RDD 的一行，其中每一个 `InternalRow` 表示 Nebula Graph 中的一行数据，最终通过分区迭代的形式读取 Nebula Graph 所有数据并组装成最终的 DataFrame 结果数据。

## 应用场景

使用 Spark-connector Reader 读取 Nebula Graph 数据得到 DataFrame 后，您可用于以下用途：

- 写入另一种数据库
- 分析数据
