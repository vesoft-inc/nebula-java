# HDFS 数据源相关参数

如果要导入的数据来源为 HDFS 文件，您可能需要配置以下参数。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| tags.name | 无 | string | 是 | 设置为 Nebula Graph 中的标签名称。 |
| tags.type.source | 无 | string | 是 | 指定标签源数据的格式。可以设置为以下值：<br />- parquet<br />- csv<br />- json<br />- txt<br />- orc<br /> |
| tags.type.sink | 无 | string | 是 | 指定标签数据导入 Nebula Graph 的方式。可以设置为：<br />- client：以客户端形式导入<br />- sst：以 SST 文件格式导入 |
| tags.path | 无 | string | 是 | 指定获取标签源数据的 HDFS 路径。路径名称可以选择以 hdfs 开头。 |
| tags.fileds | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 标签对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。 |
| tags.nebula.fields | 无 | list[string] | 是 | 指定 Nebula Graph Schema 中标签属性名称。以列表形式列出，与 `tags.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| tags.vertex <br />或者<br />tags {<br />    vertex {<br />         field: [name]<br />         policy: [uuid or hash]<br />    }<br />} | 无 | string | 是 | 将源数据中某个属性的值用作 Nebula Graph 点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `tags.vertex = field` 设置 VID。<br />如果不是上述类型的属性，您可以通过 `tags.vertex.field` 和 `tags.vertex.policy`  进行预处理，即使用 hash() 或者 uuid() 函数处理属性值，生成的数值用作 VID。其中，tags.vertex.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| tags.separator | 无 | string | 否 | 如果标签源数据为 CSV 格式，必须通过这个参数设置数据源中数据分隔方式。可以设置为：`【有特殊的规定吗？】` |
| tags.header | true | boolean | 否 | 如果标签源数据为 CSV 格式，必须通过这个参数设置源数据中是否包含标题行。<br />- `true` ：包含标题行。<br />- `false` ：不包含标题行。 |
| tags.batch | 256 | int | 是 | 单次写入 Nebula Graph 的点数据量。 |
| tags.partition | 32 | int | 是 | Spark 的分区数量。`【根据什么来判断数量】` |
| edges.name | 无 | string | 是 | 指定 Nebula Graph 中的边类型名称。 |
| edges.type.source | 无 | string | 是 | 指定边类型源数据格式。可以设置为以下值：<br />- parquet<br />- csv<br />- json<br />- txt<br />- orc |
| edges.type.sink | 无 | string | 是 | 指定边类型源数据导入 Nebula Graph 的方式。可以设置为：<br />- client：表示以客户端形式导入<br />- sst：表示以 SST 文件格式导入 |
| edges.path | 无 | string | 是 | 指定获取边类型数据的 HDFS 路径。路径名称可以选择以 `hdfs` 开头。 |
| edges.fields | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 边类型对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。 |
| edges.nebula.fields | 无 | list[string] | 是 | 指定 Nebula Graph Schema 中边类型属性名称。以列表形式列出，与 `edges.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| edges.source<br />或者 <br />edges: {<br />    source: {<br />        field: <br />        policy:    <br />    }<br />} | 无 | string | 是 | 将源数据中某个属性的值用作 Nebula Graph 指定类型边的起始点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `edges.source = fieldname` 设置起始点 VID。<br />如果不是上述类型的属性，您可以通过 `edges.source.field` 和 `edges.source.policy`  进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，edges.source.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| edges.target<br />或者<br />edges: {<br />    target: {<br />        field: <br />        policy:    <br />    }<br />} | 无 |  | 是 | 将源数据中某个属性的值用作 Nebula Graph 指定类型边的目标点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `edges.target = field` 设置目标点 VID。<br />如果不是上述类型的属性，您可以通过 `edges.target.field` 和 `edges.target.policy`  进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，edges.target.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| edges.ranking | 无 | string | 否 | 将源数据中某一列数据作为 Nebula Graph 中指定边类型的 Rank 值。`必须为 Int 或 long 类型的数据？` |
| edges.batch | 256 |  | 是 | 单次写入 Nebula Graph 的边数据量。 |
| edges.partition | 32 |  | 是 | Spark 的分区数量。`【根据什么来判断数量？】` |
