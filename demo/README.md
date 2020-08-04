# IPFS Deploy demo

End to end demo of IPFS Deploy for Java.

## Sample projects

### [project-a](project-a)
Sample project that can be deployed to Textile or Fleek.

### [project-b](project-b)
Sample project for consuming a dependency from a Textile, Fleek, or IPFS repository.

## Demo steps

### Setup

1. Clone the project: `git clone https://github.com/ericglau/ifps-deploy.git`

2. Build [ipfs-deploy-maven-plugin](../ipfs-deploy-maven-plugin) according to its instructions and configure Textile CLI and/or Fleek API keys.

3. Build [wagon-ipfs](../wagon-ipfs) according to its instructions.

### Deploying to Textile, archiving to Filecoin

4. Deploy sample `project-a` to Textile bucket.

```
cd demo/project-a
mvn clean ipfs-deploy:textile
```

5. Take note of the last line from the console output which is the URL of the Textile bucket that was created.

6. Archive the Textile bucket to Filecoin testnet.

```
mvn ipfs-deploy:archive
```

7. View Filecoin archival status. (This can take hours. You can cancel with CTRL-C and view the status again later.)

```
mvn ipfs-deploy:archive -Dstatus
```

### Using a Textile repository

This scenario demonstrates `project-b` having a compile dependency on `project-a`, where `project-a` is available on Textile.

8. Configure sample `project-b` to use your Textile bucket as a repository for its dependency by editing its `pom.xml` and changing the `<url>` value to the URL that you got in step 5.

9. Compile sample `project-b`. Notice it downloads the dependency from Textile and compiles successfully.

```
mvn clean compile -U
```

### Deploying to Fleek Storage

10. Deploy sample `project-a` to Textile bucket.  Set `-Dbucket` to your bucket name from the Fleek Storage app e.g. `-Dbucket=ericglau-team-bucket`.  The `-Ddirectory` is optional but can be used to specify a subdirectory within your Fleek bucket to upload to.

```
cd ../project-a
mvn clean ipfs-deploy:fleek -Dbucket=<your Fleek bucket name> -Ddirectory=<optional subdirectory>
```

11. Take note of the last line from the console output which is the URL of the Fleek bucket (and optional subdirectory) where the project was deployed to.

### Using a Fleek repository

This scenario demonstrates `project-b` having a compile dependency on `project-a`, where `project-a` is available on Fleek.

12. Configure sample `project-b` to use your Fleek bucket as a repository for its dependency by editing its `pom.xml` and changing the `<url>` value to the URL that you got in step 11.

13. Compile sample `project-b`. Notice it downloads the dependency from Fleek and compiles successfully.

```
mvn clean compile -U
```

### Using a native IPFS repository

This scenario demonstrates `project-b` having a compile dependency on `project-a`, where `project-a` is available at an IPFS URL e.g. `ipfs://`.

14. Clean up the local cache to ensure the subsequent step gets `project-a` from IPFS.
```
rm -rf ~/.m2/repository/com/github/ericglau/project-a
```

15. Notice `project-b` is configured with the provided [wagon-ipfs](../wagon-ipfs) build extension, which allows the build process to handle IPFS URLs.

16. Configure sample `project-b` to use an IPFS URL as a repository for its dependency by editing its `pom.xml` and changing the `<url>` value to the following:
```
<url>ipfs://bafybeib5mrm7b2bvnyx3mseeknfsxwvnqas2od2g74sqat6w3m5d3bbfhy</url>
```

17. Compile sample `project-b`. Notice it downloads the dependency from the IPFS URL and compiles successfully.

```
mvn clean compile -U
```
