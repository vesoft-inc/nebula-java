# 使用限制

本文描述 Exchange 的一些使用限制。

## 适用的 Nebula Graph 版本

Nebula Graph 1.0.1 及以上版本。

## 使用环境

Exchange 支持以下操作系统：

- CentOS 7
- Mac OS

## 软件依赖

为保证 Exchange 正常工作，确认您的机器上已经安装以下软件：

- Apache Spark：2.3.0 及以上版本
- Java：1.8
- Scala：2.10.7、2.11.12、2.12.10

在以下使用场景，还需要部署 Hadoop Distributed File System (HDFS)：

- 以客户端形式迁移 HDFS 上的数据
- 以 SST 文件格式迁移数据

## 数据格式和来源

Exchange 支持将以下格式或来源的数据转换为 Nebula Graph 能识别的点和边数据：

- 存储在 HDFS 的数据，包括：
  - Apache Parquet
  - Apache ORC
  - JSON
  - CSV
- 数据仓库：HIVE
- 图数据库：Neo4j 2.4.5-M1
- 关系型数据库：MySQL
- 流处理软件平台：Apache Kafka&reg;
- 发布/订阅消息系统：Apache Pulsar 2.4.5
