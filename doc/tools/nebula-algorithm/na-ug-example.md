# 使用示例

一般，您可以按以下步骤使用 nebula-algorithm：

1. 参考配置文件，修改 nebula-algorithm 的配置。
2. 运行 nebula-algorithm。

本文以一个示例说明如何使用 nebula-algorithm。

## 示例环境

- 三台虚拟机，配置如下：
   - CPU：Intel(R) Xeon(R) Platinum 8260M CPU @ 2.30GHz
   - Processors：32
   - CPU Cores：16
   - 内存：128 GB
- 软件环境：
   - Spark：spark-2.4.6-bin-hadoop2.7 三个节点集群
   - yarn V2.10.0：三个节点集群
   - Nebula Graph V1.1.0：分布式部署，默认配置
  
## 示例数据

在本示例中，Nebula Graph 图空间名称为 algoTest，Schema 要素如下表所示。

| 要素  |  名称 | 属性  |
| :--   | :--   | :--   |
| 标签  | `PERSON`  | 无  |
| 边类型  | `FRIEND`  | `likeness`(`double`)  |

## 前提条件

在操作之前，您需要确认以下信息：

- 已经完成 nebula-algorithm 编译。详细信息参考 [编译 nebula-algorithm](na-ug-compile.md)。
- Nebula Graph 数据库中已经有图数据。您可以使用不同的方式将其他来源的数据导入 Nebula Graph 数据库中，例如 [Spark Writer](https://docs.nebula-graph.com.cn/manual-CN/3.build-develop-and-administration/5.storage-service-administration/data-import/spark-writer/ "点击前往 Nebula Graph 网站")。
- 当前机器上已经安装 Spark 并已启动 Spark 服务。

## 第 1 步. 修改配置文件

根据项目中的 `src/main/resources/application.conf` 文件修改 nebula-algorithm 配置。

```conf
{
  # Spark 相关设置
  spark: {
    app: {
        # Spark 应用程序的名称，可选项。默认设置为您即将运行的算法名称，例如 PageRank
        name: PageRank

        # Spark 中分区数量，可选项。
        partitionNum: 12
    }

    master: local

    # 可选项，如果这里未设置，则在执行 spark-submit spark 任务时设置
    conf: {
        driver-memory: 20g
        executor-memory: 100g
        executor-cores: 3
        cores-max:6
    }
  }

  # Nebula Graph 相关配置
  nebula: {
    # 必需。Meta 服务的信息
    addresses: "127.0.0.1:45500" # 如果有多个 Meta 服务复本，则以英文逗号隔开
    user: root
    pswd: nebula
    space: algoTest
    # 必需。创建图空间时设置的分区数量。如果创建图空间时未设置分区数，则设置为默认值 100
    partitionNumber: 100
    # 必需。Nebula Graph 的边类型，如果有多种边类型，则以英文逗号隔开
    labels: ["FRIENDS"]

    hasWeight: true
    # 如果 hasWeight 配置为 true，则必须配置 weightCols。根据 labels 列出的边类型，按顺序在 weightCols 里设置对应的属性，一种边类型仅对应一个属性
    # 说明：nebula-algorithm 仅支持同构图，所以，weightCols 中列出的属性的数据类型必须保持一致而且均为数字类型
    weightCols: ["likeness"] # 如果 labels 里有多种边类型，则相应设置对应的属性，属性之间以英文逗号隔开
  }

  algorithm: {
    # 指定即将执行的算法，可以配置为 pagerank 或 louvain
    executeAlgo: louvain
    # 指定算法结果的存储路径
    path: /tmp

    # 如果选择的是 PageRank，则配置 pagerank 相关参数
    #pagerank: {
    #    maxIter: 20
    #    resetProb: 0.15  # 默认值为 0.15
    #}

    # 如果选择的是 louvain，则配置 louvain 相关参数
    #louvain: {
    #   maxIter: 20
    #    internalIter: 10
    #    tol: 0.5
   #}
  }
}
```

## 第 2 步. 执行 nebula-algorithm

运行以下命令，提交 nebula-algorithm 应用程序。

```shell
spark-submit --master "local" --class com.vesoft.nebula.tools.algorithm.Main /your-jar-path/nebula-algorithm-1.0.1.jar -p /your-application.conf-path/application.conf
```

其中，

- `--master`：指定 Spark 集群中Master 进程的 URL。详细信息，参考 [master-urls](https://spark.apache.org/docs/latest/submitting-applications.html#master-urls "点击前往 Apache Spark 文档")。
- `--class`：指定 Driver 主类。
- `-p`：Spark 配置文件文件路径。
- 其他：如果您未在配置文件中设置 Spark 的任务资源分配（`conf`）信息，您可以在这个命令中指定。例如，本示例中，`--driver-memory=20G  --executor-memory=100G --executor-cores=3`。

## 测试结果

按本示例设置的 Spark 任务资源分配，对于一个拥有一亿个数据的数据集：

- PageRank 的执行时间（PageRank 算法执行时间）为 21 分钟
- Louvain 的执行时间（Reader + Louvain 算法执行时间）为 1.3 小时
