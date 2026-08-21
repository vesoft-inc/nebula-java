# Migrating from the NebulaGraph v3 Java client

`client-v3compat` is a source-compatible re-implementation of the NebulaGraph v3 Java client
(`com.vesoft.nebula.client.graph.*`) under the `com.vesoft.nebula.driver.v3client` namespace. It
keeps the v3 API surface (class names and method signatures) and delegates internally to the v5
`driver`, so existing v3 applications can migrate to NebulaGraph v5 with minimal changes.

## Migration steps

### 1. Replace the Maven dependency

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

### 2. Rewrite the imports

Global replacement:

- `com.vesoft.nebula.client.` → `com.vesoft.nebula.driver.v3client.`
- `com.vesoft.nebula.ErrorCode` → `com.vesoft.nebula.driver.v3client.graph.ErrorCode`

### 3. Migrate the GQL from nGQL to ISO-GQL

The compatibility layer adapts the Java API and data model only — it does **not** rewrite query
text. Migrate your statements yourself. Typical rewrites:

| v3 nGQL | v5 ISO-GQL |
|---|---|
| `CREATE SPACE ... (vid_type=...)` | `CREATE GRAPH TYPE ... AS {...}` + `CREATE GRAPH ... <type>` |
| `CREATE TAG ...` / `CREATE EDGE ...` | `NODE TYPE` / `EDGE TYPE` inside `CREATE GRAPH TYPE` |
| `INSERT VERTEX ... VALUES ...` | `INSERT OR IGNORE(@node_type{...})` |
| `INSERT EDGE ... VALUES ...` | `INSERT OR IGNORE (src)-[@edge_type{...}]->(dst)` |
| `USE space;` | `USE graph` or `SESSION SET GRAPH "graph"` |
| `GO ...` / `FETCH ...` / `LOOKUP ...` | `MATCH ...` |
| `YIELD` | `RETURN` |

## Code examples

### NebulaPool + Session

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
    ResultSet.Record rec = rs.rowValues(0);
    System.out.println(rec.get("v"));
}
session.release();
pool.close();
```

### SessionPool

```java
import com.vesoft.nebula.driver.v3client.graph.SessionPool;
import com.vesoft.nebula.driver.v3client.graph.SessionPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import java.util.Arrays;

SessionPoolConfig config = new SessionPoolConfig(
        Arrays.asList(new HostAddress("127.0.0.1", 9669)), "my_graph", "root", "nebula")
    .setMaxSessionSize(10)
    .setMinSessionSize(1);
SessionPool pool = new SessionPool(config);
ResultSet rs = pool.execute("MATCH (v:player) RETURN v LIMIT 1"); // bound to my_graph
pool.close();
```

Full runnable examples: `examples/src/main/java/com/vesoft/nebula/V3CompatExample.java` and
`examples/src/main/java/com/vesoft/nebula/V3SessionPoolExample.java`.

## Coverage

Only the v3 `graph` package is covered:

- `graph`: `NebulaPoolConfig`, `NebulaSession`, `SessionPool`, `SessionPoolConfig`,
  `SessionsManagerConfig`
- `graph.data`: `ResultSet`(+`Record`), `ValueWrapper`(+`NullType`), `Node`, `Relationship`,
  `PathWrapper`(+`Segment`), `DateWrapper`/`TimeWrapper`/`DateTimeWrapper`/`DurationWrapper`/
  `GeographyWrapper` + geographic wrappers, `HostAddress`, `SSLParam` hierarchy, `TimeUtil`
- `graph.exception`: `AuthFailedException`, `BindSpaceFailedException`,
  `ClientServerIncompatibleException`, `IOErrorException`, `InvalidConfigException`,
  `InvalidSessionException`, `InvalidValueException`, `NotValidConnectionException`
- `graph.net`: `NebulaPool`, `Session`, `SessionState`, `SessionsManager`, `SessionWrapper`,
  `AuthResult` and connection-level shims (`Connection`, `SyncConnection`, `LoadBalancer`,
  `RoundRobinLoadBalancer`, `ConnObjectPool`)

The v3 `meta`, `storage`, and `encoder` packages have no v5 equivalents and are **not** provided.

## Known differences

The following v3 API details cannot be reproduced verbatim in v5:

| v3 API | Behavior in the compat layer | Reason |
|---|---|---|
| `ValueWrapper.getValue()` returns `Value` | returns the underlying v5 `ValueWrapper` (as `Object`) | v3 Thrift `Value` no longer exists |
| `ResultSet.getRows()` returns `List<Row>` | removed | v3 Thrift `Row` no longer exists |
| `ResultSet.getPlanDesc()` returns `PlanDescription` | returns the v5 `PlanInfoNode` | plan-tree type changed |
| `ResultSet.getSpaceName()` / `getComment()` | returns `""` | no equivalent field in the v5 response |
| `Node.getId()` / `Relationship.srcId()/dstId()` string vid | returns the v5 numeric id as a `ValueWrapper` (`asLong()`) | v5 ids are `long`; string ids are not recoverable |
| v3 multi-tag vertex `values(tagName)` | returns the flat property map; `tagName` is only validated | v5 is a single node type + labels with flat properties |
| `executeJson` JSON structure | generated via fastjson (approximate v3 shape) | v5 has no JSON channel |
| v5 `DECIMAL` values | surfaced via `ValueWrapper.isDouble()` / `asDouble()` | v3 had no decimal type; converted to double |

## Build & test

```bash
# unit tests
mvn -pl client-v3compat test

# integration test against a live v5 cluster
mvn -pl client-v3compat test \
  -Dnebula.it=true -Dnebula.host=<host> -Dnebula.port=<port> \
  -Dnebula.user=root -Dnebula.password=<pwd> \
  -Dtest=V3IntegrationTest
```

The integration test creates a graph type + graph, inserts nodes and edges, queries node/edge/path,
exercises `SessionPool`, and drops everything afterwards.
