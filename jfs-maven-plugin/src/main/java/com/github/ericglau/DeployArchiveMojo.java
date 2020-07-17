package com.github.ericglau;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "deployArchive")
public class DeployArchiveMojo extends BaseMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(), "mvn jfs:deploy jfs:archive");
        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(), "mvn jfs:archive -Dstatus");
    }

}