# Neo4j 数据源参数

如果要导入的数据来源为 Neo4j，您可能需要配置以下参数。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `tags.name` | 无 | `string` | 是 | 设置为 Nebula Graph 中的标签名称。 |
| `tags.type.source` | 无 | `string` | 是 | 指定标签源数据的格式。设置为 `neo4j`。 |
| `tags.type.sink` | 无 | `string` | 是 | 指定标签源数据导入 Nebula Graph 的方式。可以设置为：<br />- `client`：以客户端形式导入<br />- `sst`：以 SST 文件格式导入 |
| `tags.server` | 无 | `string` | 是 | 设置 Neo4j 数据库服务器地址，格式必须为 `bolt://<IP地址>:7687` 。 |
| `tags.user` | 无 | `string` | 是 | Neo4j 数据库登录账号。 |
| `tags.password` | 无 | `string` | 是 | Neo4j 数据库账号登录密码。 |
| `tags.encription` | `false` | `string` | 否 | 传输是否加密，默认值为 `false`，表示不加密。设置为 `true` 时，表示加密。 |
| `tags.database` | 无 | `string` | 否 | 设置源数据所在 Neo4j 数据库的名称。如果您使用 Community Edition Neo4j，不支持这个参数。 |
| `tags.exec` | 无 | `string` | 是 | 写入 Cypher 语句，从 Neo4j 数据库中检索打了某种标签的点的属性，并指定别名。<br />例如，写入 Cypher 语句 `match (n:label) return n.neo4j-field-0 as neo4j-field-0, n.neo4j-field-1 as neo4j-field-1 order by (n.neo4j-field-0)`。|
| `tags.fields` | 无 | `list[string]` | 是 | 指定源数据中与 Nebula Graph 标签对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。列出的属性名称必须与 `tags.exec` 中列出的属性名称保持一致。 |
| `tags.nebula.fields` | 无 | `list[string]` | 是 | 指定 Nebula Graph Schema 中标签对应的属性名称。以列表形式列出。与 `tags.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| `tags.vertex` <br />或者<br /><code>tags {<br />    vertex {<br />         field: [name]<br />         policy: ["hash" OR "uuid"]<br />    }<br />}</code> | 无 | `string` | 是 | 将源数据中某个属性的值用作 Nebula Graph 点 VID。必须为 int 或 long 类型的属性。<br />如果是上述类型的属性，使用 `tags.vertex = field` 设置 VID 列。<br />如果不是上述类型的属性，您可以通过 `tags.vertex.field` 和 `tags.vertex.policy`  对 VID 进行预处理，即使用 `hash()` 或者 `uuid()` 函数处理属性名称，生成的数值用作 VID。其中，`tags.vertex.policy` 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| `tags.batch` | 256 | `int` | 是 | 单次写入 Nebula Graph 的点数据量。 |
| `tags.partition` | 32 | `int` | 是 | Spark 的分区数量。 |
| `tags.check_point_path` | 无 | `string` | 否 | 用于设置保存导入进度信息的目录，用于断点续传。如果没有设置这个参数，表示不启用断点续传。 |
| `edges.name` | 无 | `string` | 是 | 指定 Nebula Graph 中的边类型名称。 |
| `edges.type.source` | 无 | `string` | 是 | 源数据格式，设置为 neo4j。 |
| `edges.type.sink` | 无 | `string` | 是 | 指定边类型源数据导入 Nebula Graph 的方式。可以设置为：<br />- `client`：以客户端形式导入<br />- `sst`：以 SST 文件格式导入<br /> |
| `edges.server` | 无 | `string` | 是 | 指定 Neo4j 数据库服务器地址，格式必须为 `bolt://<IP地址>:7687` 。 |
| `edges.user` | 无 | `string` | 是 | 指定 Neo4j 数据库登录账号。 |
| `edges.password` | 无 | `string` | 是 | 指定 Neo4j 数据库账号登录密码。 |
| `edges.exec` | 无 | `string` | 是 | 写入 Cypher 语句，表示从 Neo4j 数据库中查询关系属性。<br />例如，写入 Cypher 语句 `match (a:vertex_label)-[r:edge_label]->(b:vertex_label) return a.neo4j-source-field, b.neo4j-target-field, r.neo4j-field-0 as neo4j-field-0, r.neo4j-field-1 as neo4j-field-1 order by id(r)`。 |
| `edges.fields` | 无 | `list[string]` | 是 | 指定源数据中与 Nebula Graph 边类型对应的属性名称。以列表形式列出，多个属性名称之间以英文逗号隔开。列出的属性名称必须与 `edges.exec` 中列出的属性名称保持一致。 |
| `edges.nebula.fields` | 无 | `list[string]` | 是 | Nebula Graph Schema 中边类型对应的属性名称。以列表形式列出，与 `edges.fields` 列表中的属性名称一一对应，形成映射关系。多个属性名称之间以英文逗号隔开。 |
| `edges.source`<br />或者 <br /><code>edges: {<br />    source: {<br />        field: [name]<br />        policy:   ["hash" OR "uuid"] <br />    }<br />}</code> | 无 | `string` | 否 | 指定源数据中某个属性，将它的值用作 Nebula Graph 边的起始点 VID。必须为 `int` 或 `long` 类型的属性。<br />如果是上述类型的属性，使用 `edges.source = field` 设置起始点 VID 列。<br />如果不是上述类型的属性，您可以通过 `edges.source.field` 和 `edges.source.policy`  对 VID 进行预处理，即使用 `hash()` 或者 `uuid()` 函数处理属性名称，生成的数值用作 VID。其中，`edges.source.policy` 可以设置为：<br />- `"hash"`<br />- `"uuid"`<br /> |
| `edges.target`<br />或者<br /><code>edges: {<br />    target: {<br />        field: [name]<br />        policy: ["hash" OR "uuid"]   <br />    }<br />}</code> | 无 | `string` | 否 | 指定源数据中某个属性，将它的值用作 Nebula Graph 边的目标点 VID。必须为 `int` 或 `long` 类型的属性。<br />如果是上述类型的属性，使用 `edges.target = field` 设置目标点 VID 列。<br />如果不是上述类型的属性，您可以通过 `edges.target.field` 和 `edges.target.policy`  对 VID 进行预处理，即使用 `hash()` 或者 `uuid()` 函数处理属性名称，生成的数值用作 VID。其中，`edges.target.policy` 可以设置为：<br />- `"hash"`<br />- `"uuid"` |
| `edges.ranking` | 无 | `string` | 否 | 将源数据中某一列数据作为 Nebula Graph 中边的 Rank 值。 |
| `edges.batch` | 256 | `int` | 是 | 单次写入 Nebula Graph 的边数据量。 |
| `edges.partition` | 32 | `int` | 是 | Spark 的分区数量。 |
| `edges.check_point_path` | 无 | `string` | 否 | 用于设置保存导入进度信息的目录，用于断点续传。如果没有设置这个参数，表示不启用断点续传。 |
