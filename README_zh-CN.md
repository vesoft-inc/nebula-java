# nebula-java v1.x

[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

nebula-java v1.x 由以下项目组成：

- Nebula Java v1.x，是一个 Java 客户端，供开发人员用于连接 Nebula Graph v1.1.0 或以下版本。
    > **说明**：Nebula Java 非线程安全。

- 工具，包括 Nebula Graph Exchange、Nebula Spark Connector、Nebula Flink Connector 和 nebula-algorithm。详细信息，请前往 [nebula-java/tools 目录](tools)。

本文仅描述如何使用 Nebula Java v1.x。

## 适用的 Nebula Graph 版本

Nebula Java v1.x 仅用于连接 Nebula Graph v1.1.0 或以下版本。

如果您正在使用 Nebula Graph v2.0.0，必须使用 `master` 分支的 Nebula Java v2.0。详细信息参考 [v2.0 README](https://github.com/vesoft-inc/nebula-java)。

## 克隆 Nebula Java v1.x 源码

运行以下命令克隆 Nebula Java v1.x 源码：

```bash
git clone -b v1.0 https://github.com/vesoft-inc/nebula-java.git
```

## 使用 Nebula Java v1.x

### 前提条件

使用 Nebula Java v1.x 进行开发前，您需要确认以下信息：

- 已经安装 Java 8 以上版本。
- 已经部署 Nebula Graph v1.1.0 或以下版本。

### 修改 pom.xml

如果您使用 Maven 管理工程，必须添加以下依赖项到 `pom.xml` 文件中。使用需要的 Nebula Java v1.x 版本号来替代 `<version>` 设置。更多版本信息参考 [releases](https://github.com/vesoft-inc/nebula-java/releases)。

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.x.y</version>
</dependency>
```

### Graph 客户端示例

连接到 `nebula-graphd` 进程：

```java
GraphClient client = new GraphClientImpl("127.0.0.1", 3699);
client.setUser("user");
client.setPassword("password");
client.connect();
```

使用图空间：

```java
int code = client.switchSpace("space_test");
```

执行语句：

```java
int code = client.execute("CREATE TAG course(name string, credits int);");
```

如果查询执行成功，则返回 `0`。完整示例请参考 [Graph 客户端示例](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java)。

### Storage 客户端示例

如果仅使用 RPC 接口，则无需担心以下问题。

如果直接使用 Storage 客户端进行编码或解码，必须使用 JNI。[`nebula-utils`](https://repo1.maven.org/maven2/com/vesoft/nebula-utils/ "点击前往 Maven 中央仓库") 中已经包含 `libnebula_codec.so`。

如果这个 JNI 在您的环境中不起作用，按以下步骤编译：

1. 编译 [动态链接库](https://github.com/vesoft-inc/nebula/tree/master/src/jni)。
2. 在本地 Maven 仓库中，运行以下命令安装 JNI。

    ```bash
    mvn install:install-file -Dfile=${your-nebula-utils.jar} -DgroupId=com.vesoft -DartifactId=nebula-utils -Dversion={version} -Dpackaging=jar
    ```

关于如何直接从 nebula-storaged 进程中扫描点和边，参考 [Storage 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/StorageClientExample.java)。

### Meta 客户端示例

关于如何访问 Schema，参考 [Meta 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java)。

## FAQ

Q: 编译源码时出现以下错误：

```text
Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2
```

A: 此处需要生成一对秘钥。

```text
gpg --gen-key #generate your key pair
gpg --list-secret-keys #check if keys are generated successfully
```

或者，您也可以在编译时不验证 gpg 签名。

```shell
mvn clean compile package install -Dgpg.skip -Dmaven.javadoc.skip=true -Dtest.skip=true -Dmaven.test.skip=true
```
