[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

# nebula-java

This guide provides an overview of options for connecting to Nebula Graph for Java developer.

## Prerequisites

When developing with this Java driver, please use Java 8+. 
Depending on the version of Nebula Graph that you are connecting to, you will have to use a different version of this client.

| Nebula version | Nebula Java version |
|:--------------:|:-----------------:|
|     1.0.0-rc2     |      1.0.0-rc2     |

## Nebula Graph Java Driver

When using Maven, add this to your pom.xml file:

```
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>nebula</artifactId>
    <version>${VERSION}</version>
</dependency>
```

change ${VERSION} here. For more versions, please refer to [releases](https://github.com/vesoft-inc/nebula-java/releases).

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

If query executes seccuessfully, `0` will be returned. For a more complete example, refer to [Graph Java client example](./examples/src/main/java/com/vesoft/nebula/examples/GraphClientExample.java).

<!-- Not Ready RC2 Rlease
### Meta Client Example
### Storage Client Example
-->


