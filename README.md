# nebula-java
GraphClient API, StorageClient API and data importer of Nebula Graph, in Java.

### Setup the maven repositories

The `nebula-java` have published to `github package`, you can set the repositories at `~/.m2/settings.xml`

```xml
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>github</id>
          <name>GitHub OWNER Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/vesoft-inc/nebula</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>nebula-package</username>
      <password>80bbb985f160a4f5d7bc545bb9fdd9013b90d128</password>
    </server>
  </servers>
```


