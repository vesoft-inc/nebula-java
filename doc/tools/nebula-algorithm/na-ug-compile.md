# 编译 nebula-algorithm

依次运行以下命令下载并编译打包 nebula-algorithm。

```shell
$ git clone git@github.com:vesoft-inc/nebula-java.git
$ cd nebula-java/tools/nebula-algorithm
$ mvn package -DskipTests
```

编译成功后，您可以在当前目录里看到如下目录结构。

```text
nebula-java
    ├── pom.xml
    ├── src
    │   ├── main
    │   └── test
    └── target
        ├── classes
        ├── classes.1846592514.timestamp
        ├── maven-archiver
        ├── maven-status
        ├── nebula-algorithm-1.0.1-tests.jar
        ├── nebula-algorithm-1.0.1.jar
        ├── original-nebula-algorithm-1.0.1.jar
        ├── test-classes
        └── test-classes.1846592514.timestamp
```

在 `target` 目录下，您可以看到 `nebula-algorithm-1.0.1.jar` 文件。另外，在使用 nebula-algorithm 时，您可以参考 `target/classes/application.conf` 根据实际情况修改配置文件。详细信息请参考 [使用示例](na-ug-example.md)。