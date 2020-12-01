# nebula-java

![](https://img.shields.io/badge/language-java-orange.svg)
[![star this repo](http://githubbadges.com/star.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java)
[![fork this repo](http://githubbadges.com/fork.svg?user=vesoft-inc&repo=nebula-java&style=default)](https://github.com/vesoft-inc/nebula-java/fork)

Nebula Java is a Java client for Java developers to connect their projects to Nebula Graph.

> **NOTE**: Nebula Java is not thread-safe.

## Two branches of this repository

In this repository, you can find two branches for the source code of Nebula Java of different versions.

### The master branch

The master branch is for Nebula Java v2.0, which works with Nebula Graph v2.0 only.

This README file provides Java developers with instructions and options to connect to Nebula Graph v2.0.

### The v1.0 branch

The v1.0 branch is for Nebula Java v1.0, which works with Nebula Graph v1.1.0 and earlier versions only.

For more information, see [README of v1.0](https://github.com/vesoft-inc/nebula-java/blob/v1.0/README.md).

## Prerequisites

To use this Java driver, do a check of  these:

- Java 8+ is installed.
- Nebula Graph v2.0 is deployed. For more information, see [Deployment and installation of Nebula Graph](https://docs.nebula-graph.io/2.0/4.deployment-and-installation/1.resource-preparations/ "Click to go to Nebula Graph website").

## Graph Client Example

To connect to the `nebula-graphd` process:

1. When using Maven, add this dependency to your `pom.xml` file.

    ```xml
    <dependency>
        <groupId>com.vesoft</groupId>
        <artifactId>client</artifactId>
        <version>2.0.0-beta</version>
    </dependency>
    ```

2. Connect to Nebula Graph v2.0. This is the code example.

    ```java
    NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
    nebulaPoolConfig.setMaxConnSize(10);
    List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699)  ,
            new HostAddress("127.0.0.1", 3700));
    NebulaPool pool = new NebulaPool();
    pool.init(addresses, nebulaPoolConfig);
    Session session = pool.getSession("root", "nebula", false);
    session.execute(stmt);
    session.release();
    pool.close();
    ```

## Releases

To download previous releases of Nebula Java, visit [releases](https://github.com/vesoft-inc/nebula-java/releases).
