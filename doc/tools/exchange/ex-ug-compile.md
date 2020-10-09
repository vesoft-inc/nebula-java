# 编译 Exchange

依次运行以下命令下载并编译打包 Exchange。

```bash
git clone https://github.com/vesoft-inc/nebula-java.git
cd nebula-java/tools/exchange
mvn package -DskipTests
```

编译成功后，您可以在当前目录里看到如下目录结构。

```
.
|-- dependency-reduced-pom.xml
|-- pom.xml
|-- scripts
|   |-- README.md
|   |-- mock_data.py
|   |-- pulsar_producer.py
|   |-- requirements.txt
|   `-- verify_nebula.py
|-- src
|   `-- main
|       |-- resources
|       |-- scala
|       `-- test
`-- target
    |-- classes
    |   |-- application.conf
    |   |-- com
    |   |-- server_application.conf
    |   `-- stream_application.conf
    |-- classes.timestamp
    |-- exchange-1.0.1-javadoc.jar
    |-- exchange-1.0.1-sources.jar
    |-- exchange-1.0.1.jar
    |-- generated-test-sources
    |   `-- test-annotations
    |-- maven-archiver
    |   `-- pom.properties
    |-- maven-status
    |   `-- maven-compiler-plugin
    |-- original-exchange-1.0.1.jar
    |-- site
    |   `-- scaladocs
    |-- test-classes
    |   `-- com
    `-- test-classes.timestamp
```

在 `target` 目录下，您可以 `exchange-1.0.1.jar` 文件。另外，在迁移数据时，您可以参考 `target/classes/application.conf`、`target/classes/server_application.conf`、`target/classes/stream_application.conf` 根据实际情况修改配置文件。
