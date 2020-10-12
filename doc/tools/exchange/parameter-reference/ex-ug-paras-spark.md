# Spark 参数

在使用 Exchange 导入数据时，您可以根据需要设置 Spark 参数，详细信息的 Spark 参数信息，参考[《Apache Spark 文档》](https://spark.apache.org/docs/latest/configuration.html#application-properties "点击前往 Apache Spark 文档")。下表仅提供部分参数的配置说明。实际应用时的参数设置，参考不同来源数据的 [操作示例](../use-exchange/ex-ug-import-from-neo4j.md)。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `spark.app.name` | Spark Writer | `string` | 否 | Spark Driver Program 名称。 |
| `spark.driver.cores` | 1 | `int` | 否 | Driver 进程的核数，仅适用于集群模式。 |
| `spark.driver.maxResultSize` | 1G | `string` | 否 | 每个 Spark 操作（例如收集）中所有分区的序列化结果的上限（以字节为单位）。最小值为 1M，设为 0 则表示无限制上限。 |
| `spark.cores.max` | 无 | `int` | 否 | 当以“粗粒度”共享模式在独立部署集群或 Mesos 集群上运行时，跨集群（而非从每台计算机）请求应用程序的最大 CPU 核数。如果未设置，则默认值为 Spark 的独立集群管理器上的 `spark.deploy.defaultCores` 或 Mesos 上的 infinite（所有可用的内核）。 |
