# 欢迎使用 Nebula Flink Connector

## 什么是 Nebula Flink Connector

Nebula Flink Connector 是一个自定义的 Flink 连接器，支持 Flink 从 Nebula Graph 图数据库中读取数据（source），或者将其他外部数据源读取的数据写入 Nebula Graph 图数据库（sink）。

## 使用限制

【？】

## 自定义 source (NebulaSource)

Nebula Flink Connector 支持以 `addSource` 或者 `createInput` 方式将 Nebula Graph 图数据库注册为 Flink 的数据源（source）。其中，通过 `addSource` 读取 source 数据得到的是 Flink 的 `DataStreamSource`，表示 DataStream 的起点，而通过 `createInput` 读取 source 数据得到的是 Flink 的 DataSource。DataSource 是一个创建新数据集的 Operator，这个 Operator 会作为进一步转换的数据集。DataSource 可以通过 `withParameters` 封装配置参数进行其他操作。

NebulaSource 的实现类图如下所示。

![Nebula Flink Connector 的 source 实现类图](https://docs-cdn.nebula-graph.com.cn/nebula-java-tools-docs/fl-ug-001.png "source 实现类图")

### `addSource`

`addSource` 方式通过 `NebulaSourceFunction` 类实现，该类继承自 `RichSourceFunction` 并实现了以下方法：

- `open`：准备 Nebula Graph 的连接信息，并获取 Nebula Graph 图数据库 Meta 服务和 Storage 服务的连接。

- `close`：在数据读取完成后释放资源，并断开与 Nebula Graph 图数据库服务的连接。

- `run`：开始读取数据，并将数据填充到 `sourceContext`。

- `cancel`：取消 Flink 作业时调用这个方法以关闭资源。

### `createInput`

`createInput` 方式通过 `NebulaInputFormat` 类实现，该类继承自 `RichInputFormat` 并实现了以下方法：

- `openInputFormat`：准备 `inputFormat` 以获取连接。

- `closeInputFormat`：数据读取完成后释放资源。断开与 Nebula Graph 图数据库服务的连接。

- `getStatistics`<!--不需要描述吗？-->
- `createInputSplits`<!--不需要描述吗？-->
- `getInputSplitAssigner`<!--不需要描述吗？-->
- `open`：开始 `inputFormat` 的数据读取，将读取的数据转换为 Flink 的数据格式，构造迭代器。

- `close`：在数据读取完成后打印读取日志。

- `reachedEnd`：<!--判断？-->是否读取完成。

- `nextRecord`：通过迭代器获取下一条数据.

### 应用实践

自定义 NebulaSource 需要完成以下工作：<!--这个描述对吗？-->

1. 构造 `NebulaSourceFunction` 和 `NebulaOutputFormat`。
2. 通过 Flink 的 `addSource` 或者 `createInput` 方式将 Nebula Graph 注册为数据源。

在构造的 `NebulaSourceFunction` 和 `NebulaOutputFormat` 中分别对客户端参数和执行参数作了如下配置：

- `NebulaClientOptions` 需要配置：
  - Nebula Graph Meta 服务的 IP 地址及端口号。<!--如果有多个服务，应该逗号分隔？-->
  - Nebula Graph 图数据库的账号及其密码。<!--这个账号的权限有特殊要求吗？-->
- `VertexExecutionOptions` 需要配置：
  - 需要读取点数据的 Nebula Graph 图数据库中的图空间名称。
  - 需要读取的标签（点类型）名称。<!--一次只能一个标签？如果有多个标签，需要多次读取？如果一次能读取多个，用逗号分隔？-->
  - 要读取的标签属性。
  - 是否读取指定标签的所有属性，默认为 `false`。如果配置为 `true` 则标签属性的配置无效。
  - 单次读取的数据量限值，默认为 2000。<!--个点数据？-->
- `EdgeExecutionOptions` 需要配置：
  - 需要读取边数据的 Nebula Graph 图数据库中的图空间名称。
  - 需要读取的边类型。<!--一次只能一个边类型？如果有多个边类型，需要多次读取？如果一次能读取多个，用逗号分隔？-->
  - 需要读取的边类型属性。
  - 是否读取指定边类型的所有属性，默认为 `false`。如果配置为 `true` 则边类型属性的配置无效。
  - 单次读取的数据量限值，默认值为 2000。<!--个边数据？-->

假设需要读取点数据的 Nebula Graph 图数据库信息如下：

- Meta 服务为本地单副本部署，使用默认端口
- 图空间名称：`flinkSource`
- 标签：`player`
- 标签属性：`name` 和 `age`
- 单次最多读取 100 个点数据

以下为自定义 NebulaSource 的代码示例。

```xml
// 构造 Nebula Graph 客户端连接需要的参数
NebulaClientOptions nebulaClientOptions = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("127.0.0.1:45500")
                .build();
// 创建 connectionProvider
NebulaConnectionProvider metaConnectionProvider = new NebulaMetaConnectionProvider(nebulaClientOptions);

// 构造读取 Nebula Graph 数据需要的参数
List<String> cols = Arrays.asList("name", "age");
VertexExecutionOptions sourceExecutionOptions = new VertexExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace("flinkSource")
                .setTag(tag)
                .setFields(cols)
                .setLimit(100)
                .builder();

// 构造 NebulaInputFormat
NebulaInputFormat inputFormat = new NebulaInputFormat(metaConnectionProvider)
                .setExecutionOptions(sourceExecutionOptions);

// 方式 1 使用 createInput 方式将 Nebula Graph 注册为数据源
DataSource<Row> dataSource1 = ExecutionEnvironment.getExecutionEnvironment()
         .createInput(inputFormat);

// 方式 2 使用 addSource 方式将 Nebula Graph 注册为数据源
NebulaSourceFunction sourceFunction = new NebulaSourceFunction(metaConnectionProvider)
                .setExecutionOptions(sourceExecutionOptions);
 DataStreamSource<Row> dataSource2 = StreamExecutionEnvironment.getExecutionEnvironment()
         .addSource(sourceFunction);
```

### NebulaSource 示例程序

您可以参考 GitHub 上的示例程序 [testNebulaSource](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/org/apache/flink/FlinkDemo.java) 编写您自己的 Flink 应用程序。

以 testNebulaSource 为例：该程序以 Nebula Graph 图数据库为 source，以 Print 为 sink，从 Nebula Graph 图数据库中读取 59,671,064 条点数据后再打印。将该程序打包提交到 Flink 集群执行，结果如下图所示。

![Flink Dashboard 上显示的 testNebulaSource 执行结果](https://docs-cdn.nebula-graph.com.cn/nebula-java-tools-docs/fl-ug-002.png "testNebulaSource 执行结果")

由上图可知，source 发送数据 59,671,064 条，sink 接收数据 59,671,064 条。

## 自定义 sink (NebulaSink)

Nebula Flink Connector 支持以 `DataStream.writeUsingOutputFormat` 的方式将 Flink 数据流写入 Nebula Graph 数据库。

> **说明**：Nebula Flink Connector 使用 Flink 1.12-SNAPSHOT 开发，这个版本已经不再支持使用 `writeUsingOutputFormat` 方式定义输出端的接口，源码如下。所以，在使用自定义 NebulaSink 时，请您务必使用 `DataStream.addSink` 方式。
>
>       /** @deprecated */
>       @Deprecated
>       @PublicEvolving
>       public DataStreamSink<T> writeUsingOutputFormat(OutputFormat<T> format) {
>            return this.addSink(new OutputFormatSinkFunction(format));
>            }

Nebula Flink Connector 中实现了自定义的 `NebulaSinkFunction`，开发者通过调用 `dataSource.addSink` 方法并将 `NebulaSinkFunction` 对象作为参数传入即可实现将 Flink 数据流写入 Nebula Graph 数据库中。

NebulaSink 的实现类图如下所示。

![Nebula Flink Connector 的 sink 实现类图](https://docs-cdn.nebula-graph.com.cn/nebula-java-tools-docs/fl-ug-003.png "sink 实现类图")

最重要的两个类是 `NebulaSinkFunction`  `NebulaBatchOutputFormat`。

### `NebulaSinkFunction`

`NebulaSinkFunction` 继承自 `AbstractRichFunction` 并实现了以下方法：

- `open`：调用 `NebulaBatchOutputFormat` 的 `open` 方法以准备资源。

- `close`：调用 `NebulaBatchOutputFormat` 的 `close` 方法以释放资源。

- `invoke`：是 NebulaSink<!--这个词我改过了，原来是Sink。可以改吗？--> 中的核心方法，调用 `NebulaBatchOutputFormat` 中的 `write` 方法写入数据。

- `flush`：调用 `NebulaBatchOutputFormat` 的 `flush` 方法提交数据。

### `NebulaBatchOutputFormat`

`NebulaBatchOutputFormat` 继承自 `AbstractNebulaOutPutFormat`，而后者继承自 `RichOutputFormat`，主要实现了以下方法：

- `open`：准备 Nebula Graph 数据库的 Graph 服务<!--是指查询引擎服务吗？原文里写的是 Nebula Graphd 服务。多了个 d。所以有点不确定-->的连接，并初始化数据写入执行器 `nebulaBatchExecutor`。

- `close`：提交最后批次的数据，等待最后提交的回调结果并关闭服务连接等资源。

- `writeRecord`：核心方法，将数据写入 bufferRow 中，并在达到配置的批量写入上限时提交写入。NebulaSink 的写入操作是异步的，所以需要执行回调来获取执行结果。

- `flush`：当 bufferRow 存在数据时，将数据提交到 Nebula Graph 中。

在 `AbstractNebulaOutputFormat` 中调用了 `NebulaBatchExecutor`，用于数据的批量管理和批量提交，并通过定义回调函数接收批量提交的结果，代码如下：

```java
    /**
     * write one record to buffer
     */
    @Override
    public final synchronized void writeRecord(T row) throws IOException {
        nebulaBatchExecutor.addToBatch(row);

        if (numPendingRow.incrementAndGet() >= executionOptions.getBatch()) {
            commit();
        }
    }

    /**
     * put record into buffer
     *
     * @param record represent vertex or edge
     */
    void addToBatch(T record) {
        boolean isVertex = executionOptions.getDataType().isVertex();

        NebulaOutputFormatConverter converter;
        if (isVertex) {
            converter = new NebulaRowVertexOutputFormatConverter((VertexExecutionOptions) executionOptions);
        } else {
            converter = new NebulaRowEdgeOutputFormatConverter((EdgeExecutionOptions) executionOptions);
        }
        String value = converter.createValue(record, executionOptions.getPolicy());
        if (value == null) {
            return;
        }
        nebulaBufferedRow.putRow(value);
    }

    /**
     * commit batch insert statements
     */
    private synchronized void commit() throws IOException {
        graphClient.switchSpace(executionOptions.getGraphSpace());
        future = nebulaBatchExecutor.executeBatch(graphClient);
        // clear waiting rows
        numPendingRow.compareAndSet(executionOptions.getBatch(),0);
    }

    /**
     * execute the insert statement
     *
     * @param client Asynchronous graph client
     */
    ListenableFuture executeBatch(AsyncGraphClientImpl client) {
        String propNames = String.join(NebulaConstant.COMMA, executionOptions.getFields());
        String values = String.join(NebulaConstant.COMMA, nebulaBufferedRow.getRows());
        // construct insert statement
        String exec = String.format(NebulaConstant.BATCH_INSERT_TEMPLATE, executionOptions.getDataType(), executionOptions.getLabel(), propNames, values);
        // execute insert statement
        ListenableFuture<Optional<Integer>> execResult = client.execute(exec);
        // define callback function
        Futures.addCallback(execResult, new FutureCallback<Optional<Integer>>() {
            @Override
            public void onSuccess(Optional<Integer> integerOptional) {
                if (integerOptional.isPresent()) {
                    if (integerOptional.get() == ErrorCode.SUCCEEDED) {
                        LOG.info("batch insert Succeed");
                    } else {
                        LOG.error(String.format("batch insert Error: %d",
                                integerOptional.get()));
                    }
                } else {
                    LOG.error("batch insert Error");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("batch insert Error");
            }
        });
        nebulaBufferedRow.clean();
        return execResult;
    }
```

由于 NebulaSink 的写入是批量、异步的，所以在最后业务结束关闭（`close`）资源之前需要将缓存中的批量数据提交且等待写入操作的完成，以防在写入提交之前提前关闭 Nebula Graph 的客户端，代码如下：

```java
    /**
     * commit the batch write operator before release connection
     */
    @Override
    public  final synchronized void close() throws IOException {
        if(numPendingRow.get() > 0){
            commit();
        }
        while(!future.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.error("sleep interrupted, ", e);
            }
        }

        super.close();
    }

```

### 自定义 NebulaSink 应用实践

Flink 将处理完成的数据 sink 到 Nebula Graph 数据库时，需要将 Flink 数据流进行 map 转换成 NebulaSink 可接收的数据格式。自定义 NebulaSink 的使用方式是通过 `addSink` 的形式，

自定义 NebulaSink 需要完成以下工作：<!--这个描述对吗？-->

1. 将 Flink 数据转换成 NebulaSink 可以接受的数据格式。<!--这里需要有代码辅助说明吗？-->
2. 将 `NebulaSinkFunction` 作为参数传给 `dataSource.addSink` 方法写入 Flink 数据流。

在构造的 `NebulaSinkFunction` 中分别对客户端参数和执行参数作了如下配置：

- `NebulaClientOptions` 需要配置：
  - Nebula Graph 图数据库 Graph 服务的 IP 地址及端口号。<!--是 Graph 服务吧？如果有多个服务，应该逗号分隔？-->
  - Nebula Graph 图数据库的账号及其密码。<!--这个账号的权限有特殊要求吗？-->
- `VertexExecutionOptions` 需要配置：
  - 需要写入点数据的 Nebula Graph 图数据库中的图空间名称。
  - 需要写入的标签（点类型）名称。<!--一次只能一个标签？如果有多个标签，需要多次写入？如果一次能写入多个，用逗号分隔？-->
  - 需要写入的标签属性。
  - 需要写入的点 VID 所在 Flink 数据流 Row 中的索引
  - 需要写入的边起点 VID（src_Id）所在 Flink 数据流 Row 中的索引。如果 DataType 是点，可不配。
  - 需要写入的边终点 VID（dst_Id）所在 Flink 数据流 Row 中的索引。如果 DataType 是点，可不配。
  - 单次写入 Nebula Graph 的数据量限值，默认为 2000。<!--个点数据？-->
- `EdgeExecutionOptions` 需要配置：
  - 需要写入边数据的 Nebula Graph 图数据库中的图空间名称。
  - 需要写入的边类型。<!--一次只能一个边类型？如果有多个边类型，需要多次写入？如果一次能写入多个，用逗号分隔？-->
  - 需要写入的边类型属性。
  - 需要写入的边起点 VID（src_Id）所在 Flink 数据流 Row 中的索引。
  - 需要写入的边终点 VID（dst_Id）所在 Flink 数据流 Row 中的索引。
  - 需要写入的边 rank 所在 Flink 数据流 Row 中的索引。如果不配置，则写入边数据时不带 rank 信息。
  - 单次写入的数据量限值，默认值为 2000。<!--个边数据？-->

假设需要写入点数据的 Nebula Graph 图数据库信息如下：

- Graph 服务为本地单副本部署，使用默认端口
- 图空间名称：`flinkSink`
- 标签：`player`
- 标签属性：`name` 和 `age`
- <!--代码里 `.setIdIndex(0)` 和 `.setBatch(2)` 表示什么？-->

以下为自定义 NebulaSink 的代码示例。

```xml
/// 构造 Nebula Graph 的 Graph 服务客户端连接需要的参数
NebulaClientOptions nebulaClientOptions = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("127.0.0.1:3699")
                .build();
NebulaConnectionProvider graphConnectionProvider = new NebulaGraphConnectionProvider(nebulaClientOptions);


// 构造 Nebula Graph 写入点数据的操作参数
List<String> cols = Arrays.asList("name", "age")
ExecutionOptions sinkExecutionOptions = new VertexExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace("flinkSink")
                .setTag(tag)
                .setFields(cols)
                .setIdIndex(0)
                .setBatch(2)
                .builder();
  
// 将点数据写入 Nebula Graph
dataSource.addSink(nebulaSinkFunction);
```

### NebulaSink 示例程序

您可以参考 GitHub 上的示例程序 [testSourceSink](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/org/apache/flink/FlinkDemo.java) 编写您自己的 Flink 应用程序。

以 testSourceSink 为例：该程序以 Nebula Graph 图数据库为 source，以 Print 为 sink，从 Nebula Graph 图数据库中读取 59,671,064 条点数据后再打印。将该程序打包提交到 Flink 集群执行，结果如下图所示。以 Nebula Graph 的图空间 `flinkSource` 作为 source，通过 Flink 读取进行 `map` 类型转换后数据，再写入 Nebula Graph 另一个图空间 `flinkSink`，即 Nebula Graph 一个图空间 `flinkSource` 的数据流入另一个图空间 `flinkSink` 中。

## 特别说明：Catalog

Flink 1.11.0 之前，如果依赖 Flink 的 source/sink 读写外部数据源时，用户必须手动读取对应数据系统的 Schema（模式）。例如，如果您要读写 Nebula Graph 的数据，则必须先保证明确地知晓 Nebula Graph 中的 Schema 信息。由此带来的问题是：当 Nebula Graph 中的 Schema 发生变化时，用户需要手动更新对应的 Flink 任务以保持类型匹配，否则，任何不匹配都会造成运行时报错使作业失败，整个操作冗余且繁琐，体验极差。

Flink 1.11.0 版本后，用户使用 Flink SQL 时可以自动获取表的  Schema 而不再需要输入 DDL，即 Flink 在不了解外部系统数据的 Schema 时仍能完成数据匹配。

目前 Nebula Flink Connector 已经支持数据的读写，要实现 Schema 的匹配则需要为 Flink Connector 实现 Catalog 管理。但是，为了确保 Nebula Graph 中的数据安全，Nebula Flink Connector 仅支持 Catalog 的读操作，不允许进行 Catalog 的修改和写入。

访问 Nebula Graph 指定类型的数据时，完整路径格式如下：`<graphSpace>.<VERTEX.tag>` 或者 `<graphSpace>.<EDGE.edge>`。

具体使用方式如下：

```java
String catalogName  = "testCatalog";
String defaultSpace = "flinkSink";
String username     = "root";
String password     = "nebula";
String address      = "192.168.8.171:45500";
String table        = "VERTEX.player"

// define Nebula catalog
Catalog catalog = NebulaCatalogUtils.createNebulaCatalog(catalogName, defaultSpace, address, username, password);
// define Flink table environment
StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
tEnv = StreamTableEnvironment.create(bsEnv);
// register customed nebula catalog
tEnv.registerCatalog(catalogName, catalog);
// use customed nebula catalog
tEnv.useCatalog(catalogName);

// show graph spaces of nebula
String[] spaces = tEnv.listDatabases();

// show tags and edges of <!--没写完？-->
tEnv.useDatabase(defaultSpace);
String[] tables = tEnv.listTables();

// check tage player exist in defaultSpace
ObjectPath path = new ObjectPath(defaultSpace, table);
assert catalog.tableExists(path) == true

// get nebula tag schema
CatalogBaseTable table = catalog.getTable(new ObjectPath(defaultSpace, table));
table.getSchema();
```

关于 Catalog 接口的详细信息，参考 [Flink-table 代码](https://github.com/apache/flink/blob/master/flink-table/flink-table-common/src/main/java/org/apache/flink/table/catalog/Catalog.java)。

## 特殊说明：Exactly-once

Flink Connector 的 Exactly-once 是指 Flink 借助 checkpoint 机制保证每个输入事件只对最终结果影响一次，在数据处理过程中即使出现故障，也不会出现数据重复和丢失的情况。

为了提供端到端的 Exactly-once 语义，Flink 的外部数据系统也必须提供提交或回滚的方法，然后通过 Flink 的 checkpoint 机制协调。Flink 提供了实现端到端的 Exactly-once 的抽象，即实现二阶段提交的抽象类 `TwoPhaseCommitSinkFunction`。

如果您想为数据输出端实现 Exactly-once，则需要实现四个函数：

- `beginTransaction`：在事务开始前，在目标文件系统的临时目录中创建一个临时文件，随后可以在数据处理时将数据写入此文件。

- `preCommit`：预提交阶段。在这个阶段，刷新文件到存储，关闭文件不再写入。为下一个 checkpoint 的任何后续文件写入启动一个新事务。

- `commit`：提交阶段。在这个阶段，将预提交阶段的文件原子地移动到真正的目标目录。这个阶段会明显增加输出数据的延迟。<!--需要确认句子修改是否正确-->

- `abort`：终止阶段。在这个阶段，删除临时文件。

由以上函数可看出，Flink 的二阶段提交对外部数据源有要求，即 source 数据源必须具备重发功能，sink 数据池必须支持事务提交和幂等写。

Nebula Graph v1.1.0 虽然不支持事务，但其写入操作是幂等的，即同一条数据的多次写入结果是一致的。因此可以通过 checkpoint 机制实现 Nebula Flink Connector 的 At-least-Once 机制，根据多次写入的幂等性可以间接实现 sink 的 Exactly-once。

要使用 NebulaSink 的容错性，请确保在 Flink 的执行环境中开启了 checkpoint 配置，代码如下所示。

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.enableCheckpointing(10000) // checkpoint every 10000 msecs
   .getCheckpointConfig()
   .setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
```
