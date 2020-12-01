# nebula-java v1.0

[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

Nebula Java 是一个 Java 客户端，供 Java 开发人员用于连接 Nebula Graph。

> **说明**：Nebula Java 非线程安全。

本项目的 v1.0 分支仅包含 Nebula Java v1.0 的源代码。v1.0 仅能用于连接 Nebula Graph v1.1.0 或以前的版本。

如果您正在使用 Nebula Graph v2.0.0，必须使用 Nebula Java v2.0。详细信息参考 [v2.0 README](https://github.com/vesoft-inc/nebula-java)。

## 前提条件

使用 Nebula Java v1.0 进行开发前，您需要确认以下信息：

- 已经安装 Java 8 以上版本。
- 已经部署 Nebula Graph v1.1.0 或以前版本。

## Nebula Graph Java Driver

使用 Maven 时，添加以下依赖项到 `pom.xml` 文件：

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
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

如果查询成功执行，则返回 `0`。完整示例请参考 [Graph Java 客户端示例](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java)。

### Storage 客户端

如果仅使用 RPC 接口，则无需担心以下问题。

如果直接使用 storage 客户端进行编码或解码，必须使用 `jni` 接口。我们已经在 `nebula-utils` jar 中打包了 `libnebula_codec.so`，但是如果它在您的环境中不起作用，请编译 [动态链接库](https://github.com/vesoft-inc/nebula/tree/master/src/jni)。然后在本地 Maven 仓库中运行 `mvn install` 命令安装 `jni` jar。

参考 [Storage 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/StorageClientExample.java)。

### Meta 客户端

参考 [Meta 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java)。

## 发布版本

如果您需要下载已经发布的版本，请访问 [releases](https://github.com/vesoft-inc/nebula-java/releases)。

## FAQ

Q: 编译源码时出现以下错误。

```text
Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2
```

A: 此处需要生成一对秘钥。

```text
gpg --gen-key #generate your key pair
gpg --list-secret-keys #check if keys are generated successfully
```
