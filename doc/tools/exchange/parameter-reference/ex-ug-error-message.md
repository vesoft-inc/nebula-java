# 错误码

下表列出了您在使用 Nebula Graph&reg; Exchange 时可能会收到的错误信息。

| 错误信息 | 说明 | 解决方法 |
|------|----|------|
|You can't write vertex and source or target field config same item in janus graph ${name}, " + s"because it use to judge it is edge or vertex!      |  ??   |  ??    |
|  It shouldn't happen. Maybe something wrong ${janusGraphConfig}    |    |      |
| ${category} not support     |  数据源格式不支持。  |  当前 Nebula Graph Exchange 仅支持部分格式的源数据。详细信息，参考 [使用限制](../../about-exchange/ex-ug-limitations)    |
|  Data source ${config.category} not supported    | 数据源格式不支持。  |  当前 Nebula Graph Exchange 仅支持部分格式的源数据。详细信息，参考 [使用限制](../../about-exchange/ex-ug-limitations)    |
|Your check point file maybe broken. Please delete ${checkPointNamePrefix}.* file   | 用于存储断点续传导入进度信息的目录已失效。  | 在相应的源数据配置文件里设置新的目录。  |
|<https://github.com/vesoft-inc/nebula-java/blob/master/tools/exchange/src/main/scala/com/vesoft/nebula/tools/importer/Configs.scala#L81> |   |   |
|<https://github.com/vesoft-inc/nebula-java/blob/279277331b7332d522d8f49adc1894d0d388727a/tools/exchange/src/main/scala/com/vesoft/nebula/tools/importer/Configs.scala#L539>  |   |   |
|          LOG.error("The `name` and `type` must be specified") | 未设置标签或边类型的 `name` 和 `type` 参数。  |  在相应的源数据配置文件中完成配置。 |
|If you set check point path in ${tagName}, you must set partition<0 or not set!| 如果您设置了 `tags.check_point_path`，必须将 `tags.partition` 必须设置为小于 0 的数或者不设置。  | 禁用断点续传或者修改 `tags.partition` 设置。  |
|Source must be specified|  |   |
|If you set check point path in ${edgeName}, you must set partition<0 or not set!"|如果您设置了 `edges.check_point_path`，必须将 `edges.partition` 必须设置为小于 0 的数或者不设置。 | 禁用断点续传或者修改 `edges.partition` 设置。   |
|<https://github.com/vesoft-inc/nebula-java/blob/279277331b7332d522d8f49adc1894d0d388727a/tools/exchange/src/main/scala/com/vesoft/nebula/tools/importer/Configs.scala#L847>| |   |
|address should compose by host and port|服务器地址必须包括主机 IP 地址和端口号。|修改服务器地址格式。|
|Connection Failed| 连接失败。 | `【怎么排查？】`  |
|Switch Failed  |   ||
|Some error code: ${results.asScala.filter(_.get() != ErrorCode.SUCCEEDED).head} appear|    |   |
|too many errors|   |   |
|Some error appear| |   |
|your cypher ${config.exec} return nothing!|  根据您写入配置文件的 Cypher 语句，没有查询到任何数据。  | 修改 Cypher 语句。  |
|"Too Many Errors ${config.errorConfig.errorMaxSize}"|  |   |
|Write tag ${tagConfig.name} errors|    |   |
|Not support ${x} type use as vertex field|  |   |
|Not support ${x} type use as source field| |   |
|Not support ${x} type use as target field | |   |
|Not support ${x} type use as ranking   |   |   |
