# nebula-java

[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

本指南为 Java 开发人员提供 连接 **Nebula Graph** 的说明和选项。请注意 Nebula Java 非线程安全。

## 前提条件

使用此 Java 驱动程序进行开发时，请使用 Java 8 以上版本。请根据您要连接的 **Nebula Graph** 的版本选择此客户端的版本。

| Nebula 版本 | Nebula Java 版本 |
|:--------------:|:-----------------:|
|     1.0.0      |      1.0.0        |

## Nebula Graph Java Driver

使用 Maven 时，请将如下依赖项添加到 `pom.xml` 文件：

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
</dependency>
```

版本信息请参考 [releases](https://github.com/vesoft-inc/nebula-java/releases)。

### Graph 客户端示例

连接到 `graphd`：

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

If query executes successfully, `0` will be returned. For a more complete example, refer to [Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).
如果查询成功执行，则返回 `0`。完整示例请参考 [Graph Java 客户端示例][Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java)。

### Storage 客户端

如果仅使用 RPC 接口，则无需担心以下问题。

如果直接使用 storage 客户端进行编码/解码，则需要使用 jni 接口。我们已经在`nebula-utils` jar 中打包了 `libnebula_codec.so`，但是如果它在您的环境中不起作用，请编译[动态链接库](https://github.com/vesoft-inc/nebula/tree/master/src/jni)。然后在本地 Maven 仓库中使用 `mvn install` 命令安装 jni jar。

参见 [Storage 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/StorageClientExample.java)。

### Meta 客户端

参见 [Meta 客户端示例](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java)。

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
