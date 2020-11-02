# 导入 SST 文件

Nebula Graph Exchange 能将不同来源的数据转换成 SST 文件后再导入 Nebula Graph 数据库中。本文描述 Exchange 将源数据转换为 SST 文件并导入 Nebula Graph 的实现原理，并提供示例说明如何修改配置文件完成 SST 文件导入操作。

## 实现方法

Nebula Graph 底层使用 RocksDB 作为键值型存储引擎。RocksDB 是基于磁盘的存储引擎，数据以 Sorted String Table（SSTable）格式存放。SSTable 是一个内部包含了任意长度、排好序的键值对 &lt;key,value&gt; 集合的文件，用于高效地存储大量的键值型数据。

RocksDB 提供了一系列 API 用于创建及导入 SST 文件，有助于您快速导入海量数据。

处理 SST 文件的整个过程主要由 Exchange 的 Reader、sstProcessor 和 sstWriter 完成。整个数据处理过程如下所示：

1. Exchange 的 Reader 从数据源中读取数据。

2. sstProcessor 按照 Nebula Graph 要求的格式生成 SST 文件，存储到本地，并上传到 HDFS。SST 文件主要包含点和边两类数据，其中，

   - 表示点的键包括：分区信息、点 ID（VID）、标签类型信息和标签版本信息。
   - 表示边的键包括：分区信息、起点和终点 ID（`rsc_vid` 和 `dst_vid`）、边类型信息、边排序信息和边版本信息。
   - 对应的值主要包含各个属性键值对序列化信息。

3. SstFileWriter 创建 SST 文件：Exchange 会创建一个 SstFileWriter 对象，然后打开一个文件并插入数据。生成 SST 文件时，行数据必须严格按照增序进行写入。

4. 生成 SST 文件之后，RocksDB 通过 `IngestExternalFile()` 方法将 SST 文件导入到 Nebula Graph 之中。例如：

    ```
    IngestExternalFileOptions ifo;
    // Ingest the 2 passed SST files into the DB
    Status s = db_->IngestExternalFile({"/home/usr/file1.sst", "/home/usr/file2.sst"}, ifo);
    if (!s.ok()) {
      printf("Error while adding file %s and %s, Error %s\n",
             file_path1.c_str(), file_path2.c_str(), s.ToString().c_str());
      return 1;
    }
    ```

    调用 `IngestExternalFile()` 方法时，RocksDB 默认会将文件拷贝到数据目录，并且阻塞 RocksDB 写入操作。如果 SST 文件中的键范围覆盖了 Memtable 键的范围，则将 Memtable 落盘（flush）到硬盘。将 SST 文件放置在 LSM 树最优位置后，为文件分配一个全局序列号，并打开写操作。

## 使用示例

不同来源的数据，导入 Nebula Graph 的操作与客户端形式导入操作基本相同，但是有以下差异：

- 环境里必须部署 HDFS。
- 在配置文件中，必须做以下修改：
  - 源数据的标签和边类型配置：`tags.type.sink` 和 `edges.type.sink` 必须配置为 `sst`。
  - Nebula Graph 相关配置里，需要添加 Nebula Graph 数据库 Meta 服务的 IP 地址和端口，并添加 SST 文件在本地和 HDFS 的存储路径。
  
    ```conf
      # Nebula Graph 相关配置
      nebula:  {
        addresses: ["127.0.0.1:3699"]
        # 添加 Nebula Graph 数据库 Meta 服务的 IP 地址和端口
        meta.addresses:["127.0.0.1:45500"] 
        user: user
        pswd: password
        space: test
        path:{
          # 指定 SST 文件保存到本地的路径
          local:/Users/example/Documents/tmp
          # 指定上传 SST 文件的 HDFS 路径
          remote:/example/
        }

        connection {
          timeout: 3000
          retry: 3
        }

        execution {
          retry: 3
        }

        error: {
          max: 32
          output: /tmp/errors
        }

        rate: {
          limit: 64M
          timeout: 1000
        }
      }
    ```

详细描述请参考不同数据源的操作示例：

- [导入 Neo4j 数据](ex-ug-import-from-neo4j.md)
- 导入 HDFS 数据[TODO]
- 导入 HIVE 数据[TODO]
- 导入 JanusGraph 数据[TODO]
- 导入 Kafka 数据[TODO]
- 导入 MySQL 数据[TODO]
