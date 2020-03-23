[![star this repo](http://githubbadges.com/star.svg?user=com.vesoft-inc&repo=nebula-java&style=default)](https://github.com/com.vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=com.vesoft-inc&repo=nebula-java&style=default)](https://github.com/com.vesoft-inc/nebula-java/fork)

# nebula-java

This guide provides instructions and options for connecting **Nebula Graph** for Java developer.

## Prerequisites

When developing with this Java driver, please use Java 8+. Depending on the version of **Nebula Graph** that you are connecting to, you will have to use a different version of this client.

| Nebula version | Nebula Java version |
|:--------------:|:-----------------:|
|     rc2     |      1.0.0-rc2     |
|     rc3     |      1.0.0-rc3.20200207     |

## Nebula Graph Java Driver

When using Maven, add dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0-rc3.20200207</version>
</dependency>
```

Change the `${VERSION}` here. For more information about versions, please refer to [releases](https://github.com/com.vesoft-inc/nebula-java/releases).

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

If query executes successfully, `0` will be returned. For a more complete example, refer to [Graph Java client example](./examples/src/main/java/com/com.vesoft/nebula/examples/GraphClientExample.java).

### Storage Client

If you only use the interface of RPC, nothing to worry about.

If you want to directly use storage client to encode/decode, you need to use the jni interface. We have already package a `libnebula_codec.so` in the `nebula-utils` jar, but if it doesn't works in your environment, please compile the [dynamic link library](https://github.com/com.vesoft-inc/nebula/tree/master/src/jni). And `mvn install` the jni jar in your local maven repo.
