# Kafka 数据源参数

如果要导入的数据来源为 Kafka，您可能需要配置以下参数。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| tags.name | 无 | string | 是 | 设置为 Nebula Graph 中的标签名称。 |
| tags.type.source | 无 | string `【这个数据类型对吗？】` | 是 | 指定标签源数据的格式。设置为 kafka。 |
| tags.type.sink | 无 | string `【这个数据类型对吗？】`| 是 | 指定标签源数据导入 Nebula Graph 的方式。可以设置为：<br />- client：以客户端形式导入<br />- sst：以 SST 文件格式导入 |
| tags.service`【这里是server还是 service？repo里这两个都存在。这里用的是 .conf 文件里的词】` | 无 | string | 是 | 设置点的源数据所在 Kafka 服务器的 IP 地址。 |
| tags.topic | 无 | string | 是 | 设置点的源数据所在的 Kafka Topic 名称。 |
| tags.fields | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 标签对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。 |
| tags.nebula.fields | 无 | list[string] | 是 | 指定 Nebula Graph Schema 中标签对应的属性名称。以列表形式列出。与 `tags.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| tags.vertex <br />或者<br />tags {<br />    vertex {<br />         field: [name]<br />         policy: [uuid or hash]<br />    }<br />} | 无 | string | 是 | 将源数据中某个属性的值用作 Nebula Graph 点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `tags.vertex = field` 设置 VID 列。<br />如果不是上述类型的属性，您可以通过 `tags.vertex.field` 和 `tags.vertex.policy`  对 VID 进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，tags.vertex.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` `【不支持policy吧？】`|
| tags.batch | 256 | int | 是 | 单次写入 Nebula Graph 的点数据量。 |
| tags.partition | 32 | int | 是 | Spark 的分区数量。`【判断依据？】` |
| tags.interval.seconds | 30 | int | 是 | 读取 Kafka 的 Topic 在指定时间间隔内生成的数据行数。默认值为 30，单位为秒。 |
| edges.name | 无 | string | 是 | 指定 Nebula Graph 中的边类型名称。 |
| edges.type.source | 无 | string | 是 | 源数据格式，设置为 Kafka。 |
| edges.type.sink | 无 | string | 是 | 指定边类型源数据导入 Nebula Graph 的方式。可以设置为：<br />- client：以客户端形式导入<br />- sst：以 SST 文件格式导入 |
| edges.service`【这里是server还是 service？repo里这两个都存在。这里用的是 .conf 文件里的词】` | 无 | string | 是 | 设置边的源数据所在 Kafka 服务器的 IP 地址。 |
| edges.topic | 无 | string | 是 | 设置边的源数据所在的 Kafka Topic 名称。 |
| edges.fields | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 边类型对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。 |
| edges.nebula.fields | 无 | list[string] | 是 | Nebula Graph Schema 中边类型对应的属性名称。以列表形式列出，与 `edges.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| edges.source | 无 | string | 是 | 将源数据中某个边的起始点 ID 用作 Nebula Graph 边的起始点 VID。必须为 int 或 long 类型的属性。`【这里应该不需要 policy ？】`|
| edges.target | 无 |  | 是 | 将源数据中某个边的目标点 ID 用作 Nebula Graph 边的目标点 VID。必须为 int 或 long 类型的属性。`【这里应该不需要 policy ？】` |
| edges.ranking | 无 | string | 否 | 将源数据中某一列数据作为 Nebula Graph 中边的 Rank 值。`必须为 Int 或 long 类型的数据？【Janus Graph 时可以设置这个参数吗？】` |
| edges.batch | 256 |  | 是 | 单次写入 Nebula Graph 的边数据量。 |
| edges.partition | 32 |  | 是 | Spark 的分区数量。`【判断依据？】` |
| edges.interval.seconds | 30 | int | 是 | 读取 Kafka 的 Topic 在指定时间间隔内生成的数据行数。默认值为 30，单位为秒。 |
