package com.github.ericglau;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.MalformedURLException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;

public abstract class BaseMojo extends AbstractMojo {
    
    protected Log log;

    File jfsDeployDirFile = null;
    String jfsDeployDir = null;
    String jfsDeployUrl = null;

    @Component
    protected MavenProject mavenProject;

    @Component
    protected MavenSession mavenSession;

    @Component
    protected BuildPluginManager pluginManager;

    protected void init() {
        this.log = getLog();

        try {
            jfsDeployDirFile = new File(mavenProject.getBuild().getDirectory(), "jfslocal");
            jfsDeployDirFile.mkdirs();
            jfsDeployDir = jfsDeployDirFile.getAbsolutePath();
            jfsDeployUrl = jfsDeployDirFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void runCommandInteractive(String directory, String command) throws MojoExecutionException {
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

    protected void runCommand(String directory, String command, String lookForString, String enterInput) throws MojoExecutionException {
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
                        log.info(line);
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