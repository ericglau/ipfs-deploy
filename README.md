# ifps-deploy
Java tools for deploying to and reading from decentralized Maven repositories on IPFS/Filecoin.

### ipfs-deploy-maven-plugin
Maven plugin to deploy to Textile and Fleek buckets.  
- Deploy to Textile bucket: `mvn ipfs-deploy:textile`
- Archive Textile bucket to Filecoin: `mvn ipfs-deploy:archive`
- View Textile to Filecoin archival status: `mvn ipfs-deploy:archive -Dstatus`
- Deploy to Fleek bucket: `mvn ipfs-deploy:fleek -Dbucket=<your Fleek bucket name> -Ddirectory=<optional bucket directory>`

### sample/project-a
Sample project for deploying to Textile or Fleek.

### sample/project-b
Sample project for consuming a dependency from a Textile or Fleek or IPFS-native repository.

### wagon-ipfs
[Maven Wagon](https://github.com/apache/maven-wagon) extension to allow reading from a repository using `ipfs://` url format.
