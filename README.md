# nebula-java

![](https://img.shields.io/badge/language-java-orange.svg)
[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

This guide provides instructions and options for connecting **Nebula Graph** for Java developer. However, Nebula Java is not thread-safe.

## Prerequisites

When developing with this Java driver, please use Java 8+. Depending on the version of **Nebula Graph** that you are connecting to, you will have to use a different version of this client.

| Nebula version | Nebula Java version |
|:--------------:|:-----------------:|
|     1.0.0      |      1.0.0        |

## Nebula Graph Java Driver

When using Maven, add dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
</dependency>
```

For more versions, please refer to [releases](https://github.com/vesoft-inc/nebula-java/releases).

### Graph Client Example

Connect to the `graphd`:

```java
GraphClient client = new GraphClientImpl("127.0.0.1", 3699);
client.setUser("user");
client.setPassword("password");
client.connect();
```

Use a space:

```java
int code = client.switchSpace("space_test");
```

Execute a query:

```java
int code = client.execute("CREATE TAG course(name string, credits int);");
```

If query executes successfully, `0` will be returned. For a more complete example, refer to [Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).

### Storage Client

If you only use the interface of RPC, nothing to worry about.

If you want to directly use storage client to encode/decode, you need to use the jni interface. We have already package a `libnebula_codec.so` in the [nebula-utils](https://repo1.maven.org/maven2/com/vesoft/nebula-utils/) jar package.

However, if it doesn't work in your environment, you can compile it by following steps:
- Compile the [dynamic link library](https://github.com/vesoft-inc/nebula/tree/master/src/jni). 
- Then install the jni jar into your local maven repo:
```
mvn install:install-file -Dfile=${your-nebula-utils.jar} -DgroupId=com.vesoft -DartifactId=nebula-utils -Dversion={version} -Dpackaging=jar
```

See more [Storage Client Examples](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/) for how to scan edges and vertices from storage directly.

### Meta Client

See [Meta Client Example](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java) to access graph schemas.

## FAQ

Q: Error occurs when building from the source code.

```text
Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2
```

A: This means that you need to have a key to sign the jars

```text
gpg --gen-key #generate your key pair
gpg --list-secret-keys #check if keys are generated successfully
```
