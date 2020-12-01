# nebula-java v1.0

![](https://img.shields.io/badge/language-java-orange.svg)
[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

Nebula Java is a Java client for Java developers to connect their projects to Nebula Graph.

> **NOTE**: Nebula Java is not thread-safe.

This branch, v1.0, is for Nebula Java 1.0. It works with Nebula Graph v1.1.0 and earlier versions only.

If you are using Nebula Graph v2.0, go to the `master` branch. For more information, see [README of v2.0](https://github.com/vesoft-inc/nebula-java).

## Prerequisites

To use this Java client, do a check of these:

- Java 8+ is installed.
- Nebula Graph v1.1.0 or earlier versions is deployed.

## Nebula Graph Java Client

When using Maven, add this dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Graph Client Example

Connect to the `nebula-graphd` process:

```java
GraphClient client = new GraphClientImpl("127.0.0.1", 3699);
client.setUser("user");
client.setPassword("password");
client.connect();
```

Use a graph space:

```java
int code = client.switchSpace("space_test");
```

Execute a query:

```java
int code = client.execute("CREATE TAG course(name string, credits int);");
```

If a query is executed successfully, `0` will be returned. For a more complete example, see [Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).

### Storage Client

If you only use the interface of RPC, nothing need to be done.

If you want to directly use the storage client to encode or decode, you must use the `jni` interface. We have already packaged `libnebula_codec.so` in the [nebula-utils](https://repo1.maven.org/maven2/com/vesoft/nebula-utils/) jar package.

However, if it doesn't work in your environment, follow these steps to compile it:

1. Compile the [dynamic link library](https://github.com/vesoft-inc/nebula/tree/master/src/jni).
2. Install the `jni` jar into your local maven repo.

    ```bash
    mvn install:install-file -Dfile=${your-nebula-utils.jar} -DgroupId=com.vesoft -DartifactId=nebula-utils -Dversion={version} -Dpackaging=jar
    ```

See more [Storage Client Examples](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/) about how to scan edges and vertices from storage directly.

### Meta Client

See [Meta Client Example](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java) to access graph schemas.

## Releases

To download previous releases of Nebula Java, visit [releases](https://github.com/vesoft-inc/nebula-java/releases).

## FAQ

Q: An error occurs during the process of building from the source code.

```text
Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2
```

A: You must generate a key to sign the jars

```text
gpg --gen-key #generate your key pair
gpg --list-secret-keys #check if keys are generated successfully
```
