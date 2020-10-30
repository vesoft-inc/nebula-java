# 什么是 nebula-algorithm

nebula-algorithm 是一款基于 [GraphX](https://spark.apache.org/graphx/) 的 Spark 应用程序，提供了 PageRank 和 Louvain 社区发现的图计算算法。使用 nebula-algorithm，您能以提交 Spark 任务的形式对 Nebula Graph 数据库中的数据执行图计算。

目前 nebula-algorithm 仅提供了 PageRank 和 Louvain 社区发现算法。如果您有其他需求，可以参考本项目，编写 Spark 应用程序调用 GraphX 自带的其他图算法，如 LabelPropagation、ConnectedComponent 等。

## 实现方法

nebula-algorithm 根据以下方式实现图算法：

1. 从 Nebula Graph 数据库中读取图数据并处理成 DataFrame。
2. 将 DataFrame 转换为 Graphx 的图。
3. 调用 Graphx 提供的图算法（例如 PageRank）或者您自己实现的算法（例如 Louvain 社区发现）。

详细的实现方式，您可以参考 [LouvainAlgo.scala 和 PageRankAlgo.scala](../../tools/nebula-algorithm/../../../tools/nebula-algorithm/src/main/scala/com/vesoft/nebula/tools/algorithm/lib)。

## PageRank 和 Louvain 简介

### PageRank

GraphX 的 PageRank 算法基于 Pregel 计算模型，该算法流程包括 3 个步骤：

1. 为图中每个顶点（如网页）设置一个相同的初始 PageRank 值。
2. 第一次迭代：沿边发送消息，每个顶点收到所有关联边上对点（两端顶点）的信息，得到一个新的 PageRank 值；
3. 第二次迭代：用这组新的 PageRank 按不同算法模式对应的公式形成顶点自己新的 PageRank。

关于 PageRank 的详细信息，参考 [Wikipedia PageRank 页面](https://zh.wikipedia.org/wiki/PageRank "点击前往 Wikipedia 页面")。

### Louvain

Louvain 是基于模块度（Modularity）的社区发现算法，通过模块度来衡量一个社区的紧密程度，属于图的聚类算法。如果一个顶点加入到某一社区中会使得该社区的模块度相比其他社区有最大程度的增加，则该顶点就应当属于该社区。如果加入其它社区后没有使其模块度增加，则留在自己当前社区中。详细信息，您可以参考论文《Fast unfolding of communities in large networks》。

Louvain 算法包括两个阶段，其流程就是这两个阶段的迭代过程。

1. 阶段一：不断地遍历网络图中的顶点，通过比较顶点给每个邻居社区带来的模块度的变化，将单个顶点加入到能够使 Modulaity 模块度有最大增量的社区中。例如，顶点 v 分别加入到社区 A、B、C 中，使得三个社区的模块度增量为 -1、1、2，则顶点 v 最终应该加入到社区 C 中。
2. 阶段二：对第一阶段进行处理，将属于同一社区的顶点合并为一个大的超点重新构造网络图，即一个社区作为图的一个新的顶点。此时两个超点之间边的权重是两个超点内所有原始顶点之间相连的边权重之和，即两个社区之间的边权重之和。

整个 Louvain 算法就是不断迭代第一阶段和第二阶段，直到算法稳定（图的模块度不再变化）或者到达最大迭代次数。

## 使用场景

您可以将 PageRank 算法应用于以下场景：

- 社交应用的相似度内容推荐：在对微博、微信等社交平台进行社交网络分析时，可以基于 PageRank 算法根据用户通常浏览的信息以及停留时间实现基于用户的相似度的内容推荐。
- 分析用户社交影响力：在社交网络分析时根据用户的 PageRank 值进行用户影响力分析。
- 文献重要性研究：根据文献的 PageRank 值评判该文献的质量，PageRank 算法就是基于评判文献质量的想法来实现设计。

您可以将 Louvain 算法应用于以下场景：

- 金融风控：在金融风控场景中根据用户行为特征进行团伙识别。
- 社交网络：基于网络关系中点对（一条边连接的两个顶点）之间关联的广度和强度进行社交网络划分；对复杂网络分析、电话网络分析人群之间的联系密切度。
- 推荐系统：基于用户兴趣爱好的社区发现，可以根据社区并结合协同过滤等推荐算法进行更精确有效的个性化推荐。
