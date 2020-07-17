package com.github.ericglau;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

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
        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(),
                "mvn install org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy -DaltDeploymentRepository=jfs-local-deploy::"
                        + url);
    }

    public void initBucket(String directory, String artifactId) throws MojoExecutionException {
        log.info("Deploying to Textile...");
        String threadLinkLine = runCommand(directory, "hub buck init", "Enter a name for your new bucket", "",
                "already initialized", null, "Thread link");
        runCommandInteractive(directory, "hub buck push -y");

        log.debug("LINE = " + threadLinkLine);

        String endString = threadLinkLine.substring(threadLinkLine.lastIndexOf("/") + 1);
        log.debug("endString = " + endString);

        String splitEnd = endString.split("[^a-zA-Z\\d]")[0];
        log.debug("splitEnd = " + splitEnd);

        int endOfSplitEndIndex = threadLinkLine.indexOf(splitEnd) + splitEnd.length();

        String url = threadLinkLine.substring(threadLinkLine.indexOf("http"), endOfSplitEndIndex);

        log.info("Bucket URL: " + url);

        // get redirected url
        try {
            log.info(getFinalURL(new URL(url)).toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static URL getFinalURL(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();
            int resCode = con.getResponseCode();
            if (resCode == 308) {
                String Location = con.getHeaderField("Location");
                if (Location.startsWith("/")) {
                    Location = url.getProtocol() + "://" + url.getHost() + Location;
                }
                return getFinalURL(new URL(Location));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return url;
    }

}