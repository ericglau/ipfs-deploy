package com.github.ericglau;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "archive", defaultPhase = LifecyclePhase.INITIALIZE)
public class ArchiveMojo extends BaseMojo {

    @Parameter(property = "status", defaultValue = "false")
    private boolean status;

    @Parameter(property = "info", defaultValue = "false")
    private boolean info;

    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

        if (status && info) {
            throw new MojoExecutionException("status and info flags cannot both be true");
        } else if (status) {
            runCommandInteractive(jfsDeployDir, "hub buck archive status -w");
        } else if (info) {
            runCommandInteractive(jfsDeployDir, "hub buck archive info");
        } else {
            runCommand(jfsDeployDir, "hub buck archive", "Archives are currently saved on an experimental test network", "y");
            // TOD while "bucket FIL balance is zero" 
        }
    }

}