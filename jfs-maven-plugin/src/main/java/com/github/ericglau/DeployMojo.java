package com.github.ericglau;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployMojo extends BaseMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

        runMavenDeploy(jfsDeployUrl);

        initBucket(jfsDeployDir, mavenProject.getArtifactId());
    }

    private void runMavenDeploy(String url) throws MojoExecutionException {
        log.info("JFS local deploy url: " + jfsDeployUrl);
        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(), "mvn install org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy -DaltDeploymentRepository=jfs-local-deploy::" + url);
    }

    public void initBucket(String directory, String artifactId) throws MojoExecutionException {
        log.info("Deploying to Textile...");
        runCommand(directory, "hub buck init", "Enter a name for your new bucket", "", "already initialized", null);
        runCommandInteractive(directory, "hub buck push -y");
    }

}