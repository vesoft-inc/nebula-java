# nebula-java

[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

本指南为 Java 开发人员提供连接 **Nebula Graph** 的说明和选项。

## 前提条件

使用此 Java 驱动程序进行开发时，请使用 Java 8+ 以上版本。请根据您要连接的 **Nebula Graph** 版本选择此 Java 客户端的版本。

| Nebula version | Nebula Java version |
|:--------------:|:-----------------:|
|     1.0.0-rc2     |      1.0.0-rc2     |

## Nebula Graph Java Driver

When using Maven, add dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>${VERSION}</version>
</dependency>
```

Change the `${VERSION}` here. For more information about versions, please refer to [releases](https://github.com/vesoft-inc/nebula-java/releases).

### Graph Client Example

Connect to the `graphd`:

```java
GraphClient client = new GraphClientImpl("127.0.0.1", 3699);
client.connect("user", "password");
```

Use a space:

```java
int code = client.switchSpace("space_test");
```

Execute a query:

```java
int code = client.execute("CREATE TAG course(name string, credits int);");
```

If query executes successfully, `0` will be returned. For more detailed example, please refer to [Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).

