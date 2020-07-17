package com.github.ericglau;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.Executors;

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

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project = null;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("dir = " + directory);

        initBucket(directory, project.getArtifactId());
    }

    public void initBucket(String directory, String artifactId) throws MojoExecutionException {
        runCommand(directory, "pwd", "Enter a name for your new bucket", "");
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
            //builder.redirectOutput(Redirect.INHERIT);
            builder.redirectError(Redirect.INHERIT);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            OutputStream outputStream = process.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            if (lookForString != null) {
                try {
                    for (String line; (line = reader.readLine()) != null;) {
                        System.out.println("FOUND OUTPUT: " + line);
                        if (line.contains(lookForString)) {
                            System.out.println("FOUND STRING: " + lookForString);
                            writer.write(enterInput + "\n");
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException("IO EXCEPTION " + e.getMessage(), e);
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