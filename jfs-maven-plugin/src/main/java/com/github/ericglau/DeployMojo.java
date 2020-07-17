package com.github.ericglau;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INITIALIZE)
public class DeployMojo extends AbstractMojo {

    @Parameter(property = "directory")
    private String directory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("dir = " + directory);
    }
}