# ifps-deploy
Java tools for deploying to and reading from decentralized Maven repositories on [IPFS](https://ipfs.io/)/[Filecoin](https://filecoin.io/).

## Pre-requisites
- [Java](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/)

## Components

### [ipfs-deploy-maven-plugin](ipfs-deploy-maven-plugin)
Maven plugin for deploying a Java project to Textile or Fleek buckets. Supports archiving to Filecoin testnet.

### [wagon-ipfs](wagon-ipfs)
Maven Wagon extension to allow reading from a repository using `ipfs://` url format.

### [demo](demo)
End to end demo.

#### [demo/project-a](demo/project-a)
Sample project that can be deployed to Textile or Fleek.

#### [demo/project-b](demo/project-b)
Sample project for consuming a dependency from a Textile, Fleek, or IPFS repository.
