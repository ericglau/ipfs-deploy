# ipfs-deploy-maven-plugin
Maven plugin to deploy to Textile and Fleek buckets.  

### Build
`mvn install`

### Textile requirements:
- [Textile Hub CLI](https://docs.textile.io/) must be installed and logged in. (Hub Next required for archiving to Filecoin testnet)

### Fleek requirements:
- Enter your [Fleek API key and secret](https://docs.fleek.co/storage/storage-aws-s3-integration/#getting-an-api-key) in `~/.aws/credentials` according to [AWS SDK documentation](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).

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
