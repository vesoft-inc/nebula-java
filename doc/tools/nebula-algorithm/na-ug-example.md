# 使用示例

您可以搭配 Spark Writer 使用 nebula-algorithm。一般按以下步骤操作：

1. 按 Spark Writer 的要求准备源数据。
2. 在 Nebula Graph 数据库中创建图空间和 Schema。
3. 使用 Spark Writer 将源数据导入 Nebula Graph 数据库。
4. 配置 nebula-algorithm。
5. 运行 nebula-algorithm。

本文以一个示例说明如何使用 nebula-algorithm。

## 示例环境

- 三台虚拟机，配置如下：
   - CPU：Intel(R) Xeon(R) Platinum 8260M CPU @ 2.30GHz
   - Processors：32
   - CPU Cores：16
   - Memory Size：128G
- 软件环境
   - Spark：spark-2.4.6-bin-hadoop2.7 三个节点集群
   - yarn V2.10.0：三个节点集群
   - Nebula Graph V1.0.0：分布式部署，默认配置
  
## 示例数据

在本示例中，数据要素如下：

| 要素  |  名称 | 属性  |
| :--   | :--   | :--   |
| 标签  | `PERSON`  | 无  |
| 边类型  | `FRIEND`  | `likeness`(`double`)  |

## 前提条件

在操作之前，您需要确认以下信息：

- 已经编译 nebula-algorithm。详细信息参考 [编译 nebula-algorithm](na-ug-compile.md)。
- 已经使用 Spark Writer。详细信息参考 [Spark Writer](https://docs.nebula-graph.com.cn/manual-CN/3.build-develop-and-administration/5.storage-service-administration/data-import/spark-writer/ "点击前往 Nebula Graph 网站")。
- 当前机器上已经安装 Spark 并已启动 Spark 服务。

## 第 1 步. 准备源数据

根据数据源不同，按 Spark Writer 要准备源数据。

## 第 2 步. 创建图空间和 Schema

在 Nebula Graph 数据库中创建一个名为 `algoTest` 的图空间，并根据示例数据要素创建 Schema。

  ```nGQL
  CREATE SPACE algoTest(partition_num=100, replica_factor=1); -- 创建图空间
  CREATE TAG PERSON(); -- 创建标签
  CREATE EDGE FRIEND(likeness double); -- 创建边类型
  ```

## 第 3 步. 导入数据

使用 Spark Writer 将数据离线导入 Nebula Graph 数据库中。详细信息参考 [Spark Writer](https://docs.nebula-graph.com.cn/manual-CN/3.build-develop-and-administration/5.storage-service-administration/data-import/spark-writer/ "点击前往 Nebula Graph 网站")。

## 第 4 步. 修改配置文件

根据项目中的 `src/main/resources/application.conf` 文件修改配置。

```conf
{
  # Spark 相关设置
  spark: {
    app: {
        # Spark 应用的名称，可选。默认为您将运行的算法名称，例如 PageRank
        name: PageRank

        # Spark 中分区数量，可选。
        partitionNum: 12
    }

    # 说明：master 不能配置为 local[*] 或者 local[2]
    master: local

    # 可选
    conf: {
        driver-memory: 8g
        executor-memory: 8g
        executor-cores: 1g
        cores-max:6
    }
  }

  # Nebula Graph 相关配置
  nebula: {
    # metadata 服务器信息
    addresses: "127.0.0.1:45500"
    user: root
    pswd: nebula
    space: nb
    # 创建图空间时设置的分区数量。如果您未设置分区数，则配置为默认值 100
    partitionNumber: 100
    # Nebula Graph 的边类型
    labels: ["serve", "follow"]

    hasWeight: true
    # if hasWeight is true，then weightCols is required， and weghtCols' order must be corresponding with labels.如果 hasWeight 配置为 true，则 weightCols 是必需的，而且 weightCols 的顺序必须与标签的顺序相同？
    # Noted: the graph algorithm only supports isomorphic graphs,
    #        so the data type of each col in weightCols must be consistent and all numeric types. 说明：这个图算法(是指 PageRank?) 仅支持同构图，所以，weightCols 中列出的属性的数据类型必须保持一致而且均为数字类型？
    weightCols: ["start_year", "degree"]
  }

  algorithm: {
    # the algorithm that you are going to execute，pick one from [pagerank, louvain]即将执行的算法，可以配置为 pagerank 或 louvain
    executeAlgo: louvain
    # algorithm result path 指定算法结果将存入哪个路径
    path: /tmp

    # pagerank parameter 如果选择的是 PageRank，配置 pagerank 相关参数
    pagerank: {
        maxIter: 20
        resetProb: 0.15  # default 0.15 默认值为 0.15

    }

    # louvain parameter 如果选择的是 louvain，配置 louvain 相关参数
    louvain: {
        maxIter: 20
        internalIter: 10
        tol: 0.5
   }
  }
}
```

## 第 5 步. 执行 nebula-algorithm

运行以下命令，提交 nebula-algorithm 应用程序。

```shell
spark-submit --master xxx --class com.vesoft.nebula.tools.algorithm.Main /your-jar-path/nebula-algorithm-1.0.1.jar -p /your-application.conf-path/application.conf
```
<!--这里的 xxx 是啥？master 的配置?-->

## 测试结果

Spark 任务的资源分配为 `--driver-memory=20G  --executor-memory=100G --executor-cores=3`<!--如果按这个配置才能得到下面这个时间？-->

- PageRank 在一亿数据集上的执行时间为 21 分钟（PageRank 算法执行时间）
- Louvain 在一亿数据集上的执行时间为 1.3 小时（Reader + Louvain 算法执行时间）<!--这里的 Reader 是指 Spark-connector Reader?-->
