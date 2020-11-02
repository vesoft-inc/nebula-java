# 导入命令参数

完成数据转换后，您需要运行以下命令，将数据导入 Nebula Graph 数据库。

```bash
$SPARK_HOME/bin/spark-submit  --class com.vesoft.nebula.tools.importer.Exchange --master "local[10]" target/exchange-1.0.1.jar -c /path/to/conf/application.conf
```

下表列出了命令的相关参数。

| 参数 | 是否必需 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- |
| `--class`  | 是 | 无 | 指定 Driver 主类。 |
| `--master`  | 是 | 无 | 指定 Spark 集群中Master 进程的 URL。详细信息，参考 [master-urls](https://spark.apache.org/docs/latest/submitting-applications.html#master-urls "点击前往 Apache Spark 文档")。 |
| `-c`  / `--config`  | 是 | 无 | Spark 配置文件文件路径。 |
| `-h`  / `--hive`  | 否 | `true` | 用于指定是否支持 Hive：<br />- `true`：支持 HIVE <br />- `false`：不支持 HIVE |
| `-D`  / `--dry`  | 否 | `true` | 检查配置文件是否正确：<br />- `true`：表示开启这个功能 <br />- `false`：表示关闭这个功能 |
| `r` / `--reload`  | 否 | 无 | 表示重新加载。 |
