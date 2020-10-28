# 编译 Spark-connector Reader

逐行运行以下命令编译 Spark-connector Reader。

```bash
git clone git@github.com:vesoft-inc/nebula-java.git
cd nebula-java/tools/nebula-spark
mvn clean compile package install -Dgpg.skip -Dmaven.javadoc.skip=true
```

编译成功后，您可以在 `nebula-spark/target/` 目录下看到 `nebula-spark-1.0.1.jar` 文件，如下图所示。将这个文件复制到本地 Maven 库以下路径 `com/vesoft/nebula-spark/`。

```text
.
├── classes
│   ├── META-INF
│   │   └── services
│   └── com
│       └── vesoft
├── classes.1697742720.timestamp
├── generated-sources
│   └── annotations
├── maven-archiver
│   └── pom.properties
├── maven-status
│   └── maven-compiler-plugin
│       └── compile
├── nebula-spark-1.0.1-tests.jar
├── nebula-spark-1.0.1.jar
└── original-nebula-spark-1.0.1.jar
```
