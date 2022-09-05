# nebula-java

![](https://img.shields.io/badge/language-java-orange.svg)
[![LICENSE](https://img.shields.io/github/license/vesoft-inc/nebula-java.svg)](https://github.com/vesoft-inc/nebula-java/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/tag/vesoft-inc/nebula-java.svg?label=release)](https://github.com/vesoft-inc/nebula-java/releases)
[![GitHub release date](https://img.shields.io/github/release-date/vesoft-inc/nebula-java.svg)](https://github.com/vesoft-inc/nebula-java/releases)
[![codecov](https://codecov.io/gh/vesoft-inc/nebula-java/branch/master/graph/badge.svg?token=WQVAG6VMMQ)](https://codecov.io/gh/vesoft-inc/nebula-java)

Nebula Java is a Java client for developers to connect their projects to Nebula Graph.

Please be noted that [nebula-JDBC](https://github.com/vesoft-inc/nebula-jdbc)(based on nebula-java) is the JDBC implementing for nebula graph.

> **NOTE**: Nebula Java is not thread-safe.

## Two main branches of this repository

In this repository, you can find two branches for the source code of Nebula Java of different versions.

### The master branch

The master branch is for Nebula Java v2.0, which works with Nebula Graph v2.0 nightly.

This README file provides Java developers with instructions on how to connect to Nebula Graph v2.0.

### The v1.0 branch

In the v1.0 branch, you can find source code of these:

- Nebula Java v1.0, which works with Nebula Graph v1.1.0 and earlier versions only.
- Nebula Graph Exchange, Nebula Spark Connector, Nebula Flink Connector, and nebula-algorithm.

For more information, see [README of v1.0](https://github.com/vesoft-inc/nebula-java/blob/v1.0/README.md).

### The v2.0.0-rc branch

The v2.0.0-rc branch works with Nebula Graph v2.0.0-beta and v2.0.0-rc1, but not for the latest nightly Nebula Graph.

## Prerequisites

To use this Java client, do a check of  these:

- Java 8 or a later version is installed.
- Nebula Graph is deployed. For more information, see [Deployment and installation of Nebula Graph](https://docs.nebula-graph.io/master/4.deployment-and-installation/1.resource-preparations/ "Click to go to Nebula Graph website").

## Modify pom.xml

If you use Maven to manage your project, add the following dependency to your `pom.xml` file. 
Replace `3.0-SNAPSHOT` with an appropriate Nebula Java version. 
For more versions, visit [releases](https://github.com/vesoft-inc/nebula-java/releases).

```xml
  <dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>3.0-SNAPSHOT</version>
  </dependency>
```
There are the version correspondence between client and Nebula:

| Client version | Nebula Version |
|:--------------:|:-------------------:|
|     1.0.0      |       1.0.0         |
|     1.0.1      |     1.1.0,1.2.0     |
|     1.1.0      |     1.1.0,1.2.0     |
|     1.2.0      | 1.1.0,1.2.0,1.2.1   |
|    2.0.0-beta  |      2.0.0-beta     |
|    2.0.0-rc1   |       2.0.0-rc1     |
|    2.0.0       |    2.0.0,2.0.1      |
|    2.0.1       |    2.0.0,2.0.1      |
|    2.5.0       |    2.5.0,2.5.1      |
|    2.6.0       |    2.6.0,2.6.1      |
|    2.6.1       |    2.6.0,2.6.1      |
|    3.0.0       |    3.0.x,3.1.x      |
|  3.0-SNAPSHOT  |       nightly       |

## Graph client example

To connect to the `nebula-graphd` process of Nebula Graph:

```java
NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
nebulaPoolConfig.setMaxConnSize(10);
List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669),
        new HostAddress("127.0.0.1", 9670));
NebulaPool pool = new NebulaPool();
pool.init(addresses, nebulaPoolConfig);
Session session = pool.getSession("root", "nebula", false);
session.execute("SHOW HOSTS;");
session.release();
pool.close();
```
