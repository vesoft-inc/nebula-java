# 导入数据步骤

无论导入哪种数据，您可以按以下代码来实现使用 Exchange 向 Nebula Graph 中导入数据。

## 前提条件

开始转移数据之前，您需要确保以下信息：

- 已经安装部署了 Nebula Graph 并获取查询引擎所在服务器的 IP 地址、用户名和密码。
- 已经完成 Exchange 编译。详细信息，参考 [编译 Nebula Graph Exchange](../compile-exchange/ex-ug-compile.md)。
- 已经安装 Spark。
- 在 Nebula Graph 中创建图数据模式需要的所有信息，包括标签和边类型的名称、属性等。

## 操作步骤

按以下步骤将不同来源的数据导入 Nebula Graph 数据库：

步骤 1. 在 Nebula Graph 中构图，包括创建图空间、创建图模型。
步骤 2. 配置源数据。
步骤 3. 分别修改 Spark、Nebula Graph 和源数据配置文件。
步骤 4. 向 Nebula Graph 迁移数据。
步骤 5. （可选）在 Nebula Graph 中重构索引。

详细操作步骤，根据数据来源不同，您可以参考相应的操作示例：

- [导入 Neo4j 数据](../parameter-reference/ex-ug-paras-neo4j.md)
- [导入 SST 文件](ex-ug-import-sst.md)
- 导入 HDFS 数据【TODO】
- 导入 HIVE 数据【TODO】
- 导入 MySQL 数据【TODO】
- 导入 Kafka 数据【TODO】
- 导入 Pulsar 数据【TODO】
