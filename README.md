# nebula-java v1.x

![](https://img.shields.io/badge/language-java-orange.svg)
[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

nebula-java v1.x is composed of:  

- Nebula Java v1.x, a Java client for developers to connect their projects to Nebula Graph.
  > **NOTE**: Nebula Java is not thread-safe.

- Tools such as Nebula Graph Exchange, Nebula Spark Connector, Nebula Flink Connector, and nebula-algorithm. For more information, see [nebula-java/tools](tools).

This gives a brief introduction to Nebula Java v1.x.

## Nebula Graph supported

Nebula Java v1.x works with Nebula Graph v1.1.0 and earlier versions only.

If you are using Nebula Graph v2.0, go to the `master` branch. For more information, see [README of v2.0](https://github.com/vesoft-inc/nebula-java).

## Clone source code

Run this command to clone the source code:

```bash
git clone -b v1.0 https://github.com/vesoft-inc/nebula-java.git
```

## Use Nebula Java v1.x

### Prerequisites

To use Nebula Java v1.x, do a check of these:

- Java 8 or a later version is installed.
- Nebula Graph v1.1.0 or an earlier version is deployed.

### Modify pom.xml

If you use Maven to manage your project, add the following dependency to your `pom.xml` file. Replace `1.x.y` with an appropriate Nebula Java v1.x version. For more versions, visit [releases](https://github.com/vesoft-inc/nebula-java/releases).

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>1.x.y</version>
</dependency>
```

### Graph client example

To connect to the `nebula-graphd` process:

```java
GraphClient client = new GraphClientImpl("127.0.0.1", 3699);
client.setUser("user");
client.setPassword("password");
client.connect();
```

To use a graph space:

```java
int code = client.switchSpace("space_test");
```

To execute a query:

```java
int code = client.execute("CREATE TAG course(name string, credits int);");
```

When a query is executed successfully, `0` is returned. For a complete example, see [Graph client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).

### Storage client example

If you use the interface of RPC only, nothing need to be done.

If you want to directly use the storage client to encode and decode, you must use the JNI. You can get `libnebula_codec.so` from the [nebula-utils](https://repo1.maven.org/maven2/com/vesoft/nebula-utils/ "Click to go to Maven Central repository") jar package.

If the JNI cannot work in your environment, follow these steps to compile it:

1. Compile the [dynamic link library](https://github.com/vesoft-inc/nebula/tree/master/src/jni).
2. Install the JNI jar into your local maven repository.

    ```bash
    mvn install:install-file -Dfile=${your-nebula-utils.jar} -DgroupId=com.vesoft -DartifactId=nebula-utils -Dversion={version} -Dpackaging=jar
    ```

See more [Storage client examples](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/) about how to directly scan edges and vertices from the nebula-storaged process.

### Meta client example

See [Meta client example](https://github.com/vesoft-inc/nebula-java/blob/master/examples/src/main/java/com/vesoft/nebula/examples/MetaClientExample.java) about how to get access to graph schemas.

## FAQ

Q: An error occurs during the process of building from the source code.

```text
Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-gpg-plugin:1.6:sign (default) on project client: Exit code: 2
```

A: You must generate a key to sign the jars:

```text
gpg --gen-key #generate your key pair
gpg --list-secret-keys #check if keys are generated successfully
```

Or you can skip gpg check when you compile the source code:

```shell
mvn clean compile package install -Dgpg.skip -Dmaven.javadoc.skip=true -Dtest.skip=true -Dmaven.test.skip=true
```
