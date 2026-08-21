# NebulaGraph Java SDK for NebulaGraph 5

NebulaGraph Java SDK is a Java client for developers to connect their projects to Nebula Graph.

## How to package

* dependency: your environment must have JDK8.

```agsl
./mvnw clean package -Dmaven.test.skip=true
```

the sdk jar will be generated in client/target/driver-5.3-SNAPSHOT.jar

## Example to use Java SDK

add dependency in your pom.xml:
```agsl
        <dependency>
            <groupId>com.vesoft</groupId>
            <artifactId>driver</artifactId>
            <version>5.2.0</version>
        </dependency>
```

There are two ways to use Java SDK: get a NebulaClient from NebulaPool or get a NebulaClient by
yourself.

* Using NebulaClient through the pool:

```agsl
       NebulaPool pool = null;
        try {
            pool = NebulaPool
                    .builder(addresses, userName, password)
                    .withMaxClientSize(10)
                    .withMinClientSize(1)
                    .withConnectTimeoutMills(5000)
                    .withRequestTimeoutMills(30000)
                    .withBlockWhenExhausted(true)
                    .withMaxWaitMills(60000)
                    .build();
            NebulaClient client = pool.getClient();
            client.execute("USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, v.rate");
            pool.returnClient(client);
        } catch (Exception e) {
            throw e;
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
```

* Using NebulaClient by yourself

```agsl
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd)
                    .withAuthOptions(Collections.emptyMap())
                    .withConnectTimeoutMills(5000)
                    .withRequestTimeoutMills(30000)
                    .build();
            client.execute("USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, v.rate");
        } catch (Exception e) {
            throw e;
        } finally {
            if (client != null) {
                client.close();
            }
        }
```

## Migrate from the NebulaGraph v3 Java client

`client-v3compat` is a source-compatible re-implementation of the NebulaGraph v3 Java client
(`com.vesoft.nebula.client.*`) under the `com.vesoft.nebula.driver.v3client` namespace. It keeps the
v3 API surface (`NebulaPool` / `Session` / `SessionPool` / `ResultSet` / `ValueWrapper` / `Node` /
`Relationship` / `PathWrapper`, …) and delegates to the v5 `driver` internally, so an existing v3
application can be migrated with minimal changes.

### Steps

1. Replace the Maven dependency:

```xml
<!-- old (v3) -->
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client</artifactId>
    <version>3.x.x</version>
</dependency>

<!-- new (v5, with the v3-compatible layer) -->
<dependency>
    <groupId>com.vesoft</groupId>
    <artifactId>client-v3compat</artifactId>
    <version>5.3-SNAPSHOT</version>
</dependency>
```

2. Rewrite the imports: `com.vesoft.nebula.client.` → `com.vesoft.nebula.driver.v3client.`
   (and `com.vesoft.nebula.ErrorCode` → `com.vesoft.nebula.driver.v3client.graph.ErrorCode`).

3. Migrate the GQL statements from nGQL to ISO-GQL. This is **not** handled by the compatibility
   layer — e.g. `USE space` → `USE graph`, `INSERT VERTEX/EDGE` → `INSERT OR IGNORE`, `GO/FETCH/
   LOOKUP` → `MATCH`, and the old `MATCH` syntax → v5 `MATCH`.

### Example

```java
import com.vesoft.nebula.driver.v3client.graph.NebulaPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.net.NebulaPool;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import java.util.Arrays;

NebulaPool pool = new NebulaPool();
NebulaPoolConfig config = new NebulaPoolConfig();
config.setMaxConnSize(10);
pool.init(Arrays.asList(new HostAddress("127.0.0.1", 9669)), config);

Session session = pool.getSession("root", "nebula", false);
ResultSet rs = session.execute("MATCH (v:player) RETURN v LIMIT 1"); // ISO-GQL
if (rs.isSucceeded()) {
    System.out.println(rs.rowValues(0).get("v"));
}
session.release();
pool.close();
```

A `SessionPool` variant is shown in
`examples/src/main/java/com/vesoft/nebula/V3SessionPoolExample.java`.

### Scope and declared differences

- Only the v3 `graph` package is covered; `meta` / `storage` / `encoder` have no v5 equivalents.
- `Node.getId()` and `Relationship.srcId()/dstId()` return the v5 numeric id as a `ValueWrapper`;
  string ids are no longer recoverable in v5.
- Thrift-coupled methods (`ResultSet.getRows()`, `getPlanDesc()`, `ValueWrapper.getValue()`) are
  adapted or removed; v5 `DECIMAL` values are exposed via `ValueWrapper.isDouble()/asDouble()`.

See `migration_guide_v3.md` for the full migration guide, including GQL rewrites and the
complete list of known differences.

## Note

If your packaged jar project that imports the NebulaGraph client dependency happens
`java.lang.IllegalArgumentException: Address types of NameResolver 'unix' for '192.168.15.8:9669' not supported by transport`
exception,
please pay attention to configure transformer with the `maven-shade-plugin` plugin in your project
pom.xml.

```agsl
  <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.4.1</version>
                <configuration>
                    <!-- put your configurations here -->
                    <filters>
                        <filter>
                            <artifact>*:*</artifact>
                        </filter>
                    </filters>
                    <transformers>
                        <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer">
                        </transformer>
                    </transformers>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

## version compatibility

Here is the version correspondence between Java Driver and NebulaGraph:

| Driver Version | Nebula Version |
|:--------------:|:--------------:|
|     5.2.0      |     5.x.x      |
|     5.1.2      |     5.x.x      |
|     5.1.1      |     5.x.x      |
|     5.1.0      |     5.x.x      |
|     5.0.1      |     5.x.x      |
|     5.0.0      |     5.x.x      |

