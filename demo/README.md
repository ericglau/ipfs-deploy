# IPFS Deploy demo

End to end demo of IPFS Deploy for Java.

## Sample projects

### [demo/project-a](demo/project-a)
Sample project that can be deployed to Textile or Fleek.

### [demo/project-b](demo/project-b)
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
mvn clean install ipfs-deploy:textile
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

8. Clean up the local cache to ensure the subsequent step gets `project-a` from Textile instead of locally.

```
rm -rf ~/.m2/repository/com/github/ericglau/project-a
```

### Using a Textile repository

9. Configure sample `project-b` to use your Textile bucket as a repository for its dependency by editing its `pom.xml`, and change the `<url>` value to the URL that you got in step 5.

10. Compile sample `project-b`.

```
mvn clean compile
```

