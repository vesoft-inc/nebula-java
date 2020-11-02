# 什么是 Nebula Graph Exchange

Nebula Graph Exchange（简称为 Exchange）是一款 Apache Spark&trade; 应用，用于在分布式环境中将集群中的数据批量迁移到 Nebula Graph 中，能支持多种不同格式的批式数据和流式数据的迁移。

Exchange 由 Reader、Processor 和 Writer 三部分组成。Reader 读取不同来源的数据返回 DataFrame 后，Processor 遍历 DataFrame 的每一行，根据配置文件中 `fields` 的映射关系，按列名获取对应的值。在遍历指定批处理的行数后，Writer 会将获取的数据一次性写入到 Nebula Graph 中。下图描述了 Exchange 完成数据转换和迁移的过程。

![Nebula Graph&reg; Exchange 由 Reader、Pocessor、Writer 组成，可以完成多种不同格式和来源的数据向 Nebula Graph 的迁移](https://docs-cdn.nebula-graph.com.cn/nebula-java-tools-docs/ex-ug-001.png "Nebula Graph&reg; Exchange 转数据转换和迁移的过程")

## 适用场景

Exchange 可被用于以下场景：

- 您想将来自 Kafka、Pulsar 平台的流式数据，如日志文件、网购数据、游戏内玩家活动、社交网站信息、金融交易大厅或地理空间服务，以及来自数据中心内所连接设备或仪器的遥测数据等转化为属性图的点或边数据，并导入 Nebula Graph 数据库。
- 您想从关系型数据库（如 MySQL）或者分布式文件系统（如 HDFS）中读取批式数据，如某个时间段内的数据，将它们转化为属性图的点或边数据，并导入 Nebula Graph 数据库。
- 您想将大批量数据生成 Nebula Graph 能识别的 SST 文件，再导入 Nebula Graph 数据库。

## 产品优点

Exchange 具有以下优点：

- 适应性强：支持将多种不同格式或不同来源的数据导入 Nebula Graph 数据库，便于您迁移数据。
- 支持导入 SST：支持将不同来源的数据转换为 SST 文件，用于数据导入。
- 支持断点续传：导入数据时支持断点续传，有助于您节省时间，提高数据导入效率。
- 异步操作：会在源数据中生成一条插入语句，发送给查询服务，最后再执行 Nebula Graph 的插入操作。
- 灵活性强：支持同时导入多个标签和边类型，不同标签和边类型可以是不同的数据来源或格式。
- 统计功能：使用 Apache Spark&trade; 中的累加器统计插入操作的成功和失败次数。
- 易于使用，对用户友好：采用 HOCON（Human-Optimized Config Object Notation）配置文件格式，具有面向对象风格，便于理解和操作。
