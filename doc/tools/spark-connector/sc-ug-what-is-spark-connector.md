# 什么是 Spark Connector

Spark-connector 是 Spark 与 Nebula Graph 之间进行数据交换的一个工具，包括：

- Spark-connector Reader：为您提供了一个 Spark SQL 接口，您可以使用 Spark SQL 接口编程读取 Nebula Graph 图数据，单次读取一个点或边类型的数据，并将读取的结果组装成 Spark 的 DataFrame。参考 [Spark-connector Reader 相关信息](reader/screader-ug-what-is-sparkconnector-reader.md)。

- Spark-connector Writer：目前正在开发中。

## 贡献

Spark-connector 是一个完全开源的项目，欢迎开源爱好者通过以下方式参与：

- 前往 [Nebula Graph 论坛](https://discuss.nebula-graph.com.cn/ "点击前往“Nebula Graph 论坛") 上参与 Issue 讨论，如答疑、提供想法或者报告无法解决的问题
- 撰写或改进文档
- 提交优化代码
