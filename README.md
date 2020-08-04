# ifps-deploy
Java tools for deploying to and reading from decentralized Maven repositories on [IPFS](https://ipfs.io/)/[Filecoin](https://filecoin.io/).

## Pre-requisites
- [Java](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/)

## Components

### ipfs-deploy-maven-plugin
Maven plugin to deploy to Textile and Fleek buckets. Supports archiving to Filecoin testnet.

### samples/project-a
Sample project that can be deployed to Textile or Fleek.

### samples/project-b
Sample project for consuming a dependency from a Textile, Fleek, or IPFS repository.

### wagon-ipfs
[Maven Wagon](https://github.com/apache/maven-wagon) extension to allow reading from a repository using `ipfs://` url format.
