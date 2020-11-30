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