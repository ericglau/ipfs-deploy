# ifps-deploy
Java tools for deploying to and reading from decentralized Maven repositories on [IPFS](https://ipfs.io/)/[Filecoin](https://filecoin.io/).

## Components

### [ipfs-deploy-maven-plugin](ipfs-deploy-maven-plugin)
Maven plugin for deploying a Java project to Textile or Fleek buckets. Supports archiving to Filecoin testnet.

### [samples/project-a](samples/project-a)
Sample project that can be deployed to Textile or Fleek.

### [samples/project-b](samples/project-b)
Sample project for consuming a dependency from a Textile, Fleek, or IPFS repository.

### [wagon-ipfs](wagon-ipfs)
Maven Wagon extension to allow reading from a repository using `ipfs://` url format.
