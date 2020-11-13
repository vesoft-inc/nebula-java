# Nebula Graph 相关参数

下表列出在使用 Exchange 导入数据时您需要设置的 Nebula Graph 相关参数。实际应用时的参数设置，参考不同来源数据的 [操作示例](../use-exchange/ex-ug-import-from-neo4j.md)。

| 参数 | 默认值 | 数据类型 | 是否必需 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `nebula.address.graph` | 无 | `list[string]` | 是 | Nebula Graph 图数据库 Graph 服务的地址列表。如果有多个地址，以英文逗号分隔。 |
| `nebula.address.meta` | 无 | `list[string]` | 是 | Nebula Graph 图数据库 Meta 服务的地址列表。如果有多个地址，以英文逗号分隔。 |
| `nebula.user` | `user` | `string` | 是 | 数据库用户名，默认为 `user` 。如果 Nebula Grpah 启用了身份认证：<br />- 如果未创建不同用户，使用 `root` 。<br />- 如果已经创建了不同的用户并且分配了指定空间的角色，则使用对该空间拥有写操作权限的用户。 |
| `nebula.pswd` | `password` | `string` | 是 | 数据库用户名对应的密码，默认 `user` 的密码为 `password` 。如果 Nebula Grpah 启用了身份认证：<br />- 使用 `root` 时，密码为 `nebula` 。<br />- 使用其他用户账号时，设置账号对应的密码。 |
| `nebula.space` | 无 | `string` | 是 | 导入数据对应的图空间（Space）名称。 |
| `nebula.connection.timeout` | 3000 | `int` | 否 | Thrift 连接的超时时间，单位为 ms。 |
| `nebula.connection.retry` | 3 | `int` | 否 | Thrift 连接重试次数。 |
| `nebula.execution.retry` | 3 | `int` | 否 | nGQL 语句执行重试次数。 |
| `nebula.error.max` | 32 | `int` | 否 |  |
| `nebula.error.output` | 无 | `string` | 是 | 在 Nebula Graph 服务器上指定输出错误信息的日志路径。您可以在这个文件里查看发生的所有错误信息。 |
