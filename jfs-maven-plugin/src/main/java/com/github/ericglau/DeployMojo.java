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
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INITIALIZE)
public class DeployMojo extends AbstractMojo {

    protected Log log;

    File jfsDeployDirFile = null;
    String jfsDeployDir = null;
    String jfsDeployUrl = null;

    // @Parameter(property = "directory")
    // private String directory;

    // @Parameter(defaultValue = "${project}", required = true, readonly = true)
    // private MavenProject project = null;

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.log = getLog();

        //log.info("Deploying to directory: " + directory);

        try {
            jfsDeployDirFile = new File(mavenProject.getBuild().getDirectory(), "jfslocal");
            jfsDeployDirFile.mkdirs();
            jfsDeployDir = jfsDeployDirFile.getAbsolutePath();
            jfsDeployUrl = jfsDeployDirFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        runMavenDeploy(jfsDeployUrl);

        initBucket(jfsDeployDir, mavenProject.getArtifactId());
    }

    private void runMavenDeploy(String url) throws MojoExecutionException {
        /*DeploymentRepository deploymentRepository = new DeploymentRepository();
        deploymentRepository.setId("jfs-local-deploy");
        deploymentRepository.setUrl(jfsDeployDir);
        DistributionManagement dist = new DistributionManagement();
        dist.setRepository(deploymentRepository);
        mavenProject.setDistributionManagement(dist);*/

        // executeMojo(
        //     plugin(
        //         groupId("org.apache.maven.plugins"),
        //         artifactId("maven-jar-plugin"),
        //         version("3.2.0")
        //     ),
        //     goal("jar"),
        //     configuration(
        //     ),
        //     executionEnvironment(
        //         mavenProject,
        //         mavenSession,
        //         pluginManager
        //     )
        // );
        log.info("JFS local deploy url: " + jfsDeployUrl);
        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(), "mvn install org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy -DaltDeploymentRepository=jfs-local-deploy::" + url);

        // executeMojo(
        //     plugin(
        //         groupId("org.apache.maven.plugins"),
        //         artifactId("maven-deploy-plugin"),
        //         version("3.0.0-M1")
        //     ),
        //     goal("deploy"),
        //     configuration(
        //         element(name("altDeploymentRepository"), "jfs-local-deploy::" + jfsDeployUrl)
        //     ),
        //     executionEnvironment(
        //         mavenProject,
        //         mavenSession,
        //         pluginManager
        //     )
        // );
    }

    public void initBucket(String directory, String artifactId) throws MojoExecutionException {
        log.info("Deploying to Textile...");
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