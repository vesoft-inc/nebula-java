# MySQL 数据源参数

如果要导入的数据来源为 MySQL，您可能需要配置以下参数。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| tags.name | 无 | string | 是 | 设置为 Nebula Graph 中的标签名称。 |
| tags.type.source | 无 | string | 是 | 指定标签源数据的格式，设置为 mysql。 |
| tags.type.sink | 无 | string | 是 | 指定标签数据导入 Nebula Graph 的方式。可以设置为：<br />- client：以客户端形式导入<br />- sst：以 SST 文件格式导入 |
| tags.host | 无 | string | 是 | 指定 MySQL 数据库服务器 IP 地址。 |
| tags.port | 无 | int | 是 | 指定 MySQL 数据库服务器的端口。`【默认端口 3699 吗？】` |
| tags.database | 无 | string | 是 | 指定 MySQL 数据库名称。 |
| tags.table | 无 | string | 是 | 指定 MySQL 数据库中的表名称。 |
| tags.user | 无 | string | 是 | 指定 MySQL 数据库登录账号。 |
| tags.password | 无 | string | 是 | 指定 MySQL 数据库账号登录密码。 |
| tags.exec | 无 | string | 是 | 从 MySQL 数据库中查询 Nebula Graph 标签属性对应的列。<br />例如，写入 SQL 查询语句 `select mysql-field0, mysql-field1, mysql-field2 from database.table` |
| tags.fileds | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 标签对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。列出的属性名称必须与 `tags.exec` 中列出的属性名称保持一致。 |
| tags.nebula.fields | 无 | list[string] | 是 | 指定 Nebula Graph Schema 中标签对应的属性名称。以列表形式列出，与 `tags.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| tags.vertex <br />或者<br />tags {<br />    vertex {<br />         field: [name]<br />         policy: [uuid or hash]<br />    }<br />} | 无 | string | 是 | 将源数据中某个属性的值用作 Nebula Graph 点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `tags.vertex = field` 设置 VID。<br />如果不是上述类型的属性，您可以通过 `tags.vertex.field` 和 `tags.vertex.policy`  进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，tags.vertex.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| tags.batch | 256 | int | 是 | 单次写入 Nebula Graph 的点数据量。 |
| tags.partition | 32 | int | 是 | Spark 的分区数量。 `【判断依据？】`|
| tags.check_point_path | 无 | string | 否 | 设置这个参数即启用断点续传。`【这里对应的值是啥意思？需要啥设置？】` |
| edges.name | 无 | string | 是 | 指定 Nebula Graph 中的边类型名称。 |
| edges.type.source | 无 | string | 是 | 指定源数据格式，设置为 mysql。 |
| edges.type.sink | 无 | string | 是 | 指定数据导入 Nebula Graph 的方式。可以设置为：<br />- client：以客户端形式导入<br />- sst：以 SST 文件格式导入 |
| edges.host | 无 | string | 是 | 设置 MySQL 数据库服务器的 IP 地址。 |
| edges.port | 无 | int | 是 | 指定 MySQL 数据库服务器的端口。`【默认端口 3699 吗？】` |
| edges.database | 无 | string | 是 | 指定 MySQL 数据库名称。 |
| edges.table | 无 | string | 是 | 指定 MySQL 数据库中的表名称。 |
| edges.user | 无 | string | 是 | MySQL 数据库登录账号。 |
| edges.password | 无 | string | 是 | MySQL 数据库账号登录密码。 |
| edges.exec | 无 | string | 是 | 从 MySQL 数据库中查询 Nebula Graph 边类型属性对应的列。<br />例如，写入 SQL 查询语句 `select mysql-field0, mysql-field1, mysql-field2 from database.table` |
| edges.fields | 无 | list[string] | 是 | 指定源数据中与 Nebula Graph 边类型对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。列出的属性名称必须与 `edges.exec` 中列出的属性名称保持一致。 |
| edges.nebula.fields | 无 | list[string] | 是 | Nebula Graph Schema 中边类型对应的属性名称。以列表形式列出，与 `edges.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| edges.source<br />或者 <br />edges: {<br />    source: {<br />        field: <br />        policy:    <br />    }<br />} | 无 | string | 是 | 将源数据中某个属性的值用作 Nebula Graph 边的起始点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `edges.source = field` 设置起始点 VID。<br />如果不是上述类型的属性，您可以通过 `edges.source.field` 和 `edges.source.policy`  进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，edges.source.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| edges.target<br />或者<br />edges: {<br />    target: {<br />        field: <br />        policy:    <br />    }<br />} | 无 |  | 是 | 将源数据中某个属性的值用作 Nebula Graph 边的目标点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `edges.target = field` 设置目标点 VID 列。<br />如果不是上述类型的属性，您可以通过 `edges.target.field` 和 `edges.target.policy`  对 VID 进行预处理，即使用 hash() 或者 uuid() 函数处理属性名称，生成的数值用作 VID。其中，edges.target.policy 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| edges.ranking | 无 | string | 否 | 将源数据中某一列数据作为 Nebula Graph 中边的 Rank 值。必须为 Int 或 long 类型的数据？ |
| edges.batch | 256 |  | 是 | 单次写入 Nebula Graph 的边数据量。 |
| edges.partition | 32 |  | 是 | Spark 的分区数量。`【判断依据？】` |
| edges.check_point_path | 无 | string | 是 | 设置这个参数即启用断点续传。`【这里对应的值是啥意思？需要怎么设置？】` |
