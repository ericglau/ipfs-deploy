# ipfs-deploy-maven-plugin
Maven plugin to deploy to Textile and Fleek buckets.  

### Build
`mvn install`

### Setup
Enable the plugin by adding the following into your `pom.xml`:
```
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.ericglau</groupId>
                <artifactId>ipfs-deploy-maven-plugin</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>
```

### Usage
- Deploy the project to a Textile bucket: `mvn ipfs-deploy:textile`
- Archive Textile bucket to Filecoin: `mvn ipfs-deploy:archive`
- View Textile to Filecoin archival status: `mvn ipfs-deploy:archive -Dstatus`
- Deploy the project to a Fleek bucket: `mvn ipfs-deploy:fleek -Dbucket=<your Fleek bucket name> -Ddirectory=<optional bucket directory>`
