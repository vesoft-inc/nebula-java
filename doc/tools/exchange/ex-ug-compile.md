# 编译 Nebula Graph&reg; Exchange

依次运行以下命令下载并编译打包 Exchange。

```bash
git clone https://github.com/vesoft-inc/nebula-java.git
cd nebula-java/tools/exchange
mvn package -DskipTests
```

编译成功后，您可以看到 _target/exchange-1.0.1.jar_ 文件。
