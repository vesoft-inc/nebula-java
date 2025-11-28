#NebulaGraph Java SDK for NebulaGraph 5.0

NebulaGraph Java SDK is a Java client for developers to connect their projects to Nebula Graph.

## How to package

* dependency: your environment must have JDK8.

```agsl
./mvnw clean package -Dmaven.test.skip=true
```

the sdk jar will be generated in java/client/target/driver-5.0.0.jar

## Example to use Java SDK

add dependency in your pom.xml:
```agsl
        <dependency>
            <groupId>com.vesoft</groupId>
            <artifactId>driver</artifactId>
            <version>5.1.1</version>
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
                    .withConnectTimeoutMills(1000)
                    .withRequestTimeoutMills(30000)
                    .withBlockWhenExhausted(true)
                    .withMaxWaitMills(Long.MAX_VALUE)
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
                    .withConnectTimeoutMills(1000)
                    .withRequestTimeoutMills(3000)
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
|     5.1.1      |     5.x.x      |
|     5.1.0      |     5.x.x      |
|     5.0.1      |     5.x.x      |
|     5.0.0      |     5.x.x      |


