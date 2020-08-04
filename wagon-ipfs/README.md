# wagon-ipfs
[Maven Wagon](https://github.com/apache/maven-wagon) extension to allow reading from a repository using `ipfs://` url format.

### Build
`mvn install`

### Usage
Enable the extension by adding the following into your `pom.xml` file:
```
    <build>
        <extensions>
            <extension>
                <groupId>com.github.ericglau</groupId>
                <artifactId>wagon-ipfs</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </extension>
        </extensions>
    </build>
```

Then you can use an `ipfs://` URL as a Maven repository, for example:
```
    <repositories>
        <repository>
            <id>ipfs</id>
            <name>IPFS</name>
            <url>ipfs://bafybeib5mrm7b2bvnyx3mseeknfsxwvnqas2od2g74sqat6w3m5d3bbfhy</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <releases>
                <enabled>false</enabled>
            </releases>
        </repository>
    </repositories>
```
