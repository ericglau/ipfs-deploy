package com.github.ericglau;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INITIALIZE)
public class DeployMojo extends AbstractMojo {

    protected Log log;
    
    @Parameter(property = "directory")
    private String directory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project = null;

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.log = getLog();
        
        log.info("Deploying to directory: " + directory);

        initBucket(directory, project.getArtifactId());
    }

    public void initBucket(String directory, String artifactId) throws MojoExecutionException {
        runCommand(directory, "hub buck init", "Enter a name for your new bucket", "");
        runCommandInteractive(directory, "hub buck push -y");
    }


    private void runCommandInteractive(String directory, String command) throws MojoExecutionException {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(directory));
            builder.command("bash", "-c", command);
            builder.redirectInput(Redirect.INHERIT);
            builder.redirectOutput(Redirect.INHERIT);
            builder.redirectError(Redirect.INHERIT);
            builder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Execution of command failed", e);
        }
    }

    private void runCommand(String directory, String command, String lookForString, String enterInput) throws MojoExecutionException {
        try {

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(directory));
            builder.command("bash", "-c", command);
            builder.redirectError(Redirect.INHERIT);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            OutputStream outputStream = process.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            if (lookForString != null) {
                try {
                    for (String line; (line = reader.readLine()) != null;) {
                        log.debug("Found output: " + line);
                        if (line.contains(lookForString)) {
                            log.debug("Found string: " + lookForString);
                            writer.write(enterInput + "\n");
                            writer.flush();
                        } else if (line.contains("already initialized")) {
                            log.info("Bucket is already initialized");
                            return;
                        }
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException("IOException encountered when running command. " + e.getMessage(), e);
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                reader.close();
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new MojoExecutionException("Execution of command failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Execution of command failed", e);
        }
    }
}