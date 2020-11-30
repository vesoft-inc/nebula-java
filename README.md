# nebula-java

![](https://img.shields.io/badge/language-java-orange.svg)
[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

This guide provides instructions and options for connecting **Nebula Graph2.0** for Java developer. However, Nebula Java is not thread-safe.

## Prerequisites

When developing with this Java driver, please use Java 8+. Depending on the version of **Nebula Graph** that you are connecting to, you will have to use a different version of this client.

| Nebula version | Nebula Java version |
|:--------------:|:-----------------:|
|     2.0.0-beta |      2.0.0-beta   |

### Graph Client Example

Connect to the `graphd`:

When using Maven, add dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>2.0.0-alpha</version>
</dependency>
```

```java
NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
nebulaPoolConfig.setMaxConnSize(10);
List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699),
        new HostAddress("127.0.0.1", 3700));
NebulaPool pool = new NebulaPool();
pool.init(addresses, nebulaPoolConfig);
Session session = pool.getSession("root", "nebula", false);
session.execute(stmt);
session.release();
pool.close();
```

For more versions, please refer to [releases](https://github.com/vesoft-inc/nebula-java/releases).
