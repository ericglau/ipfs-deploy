package com.github.ericglau;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "fleek", defaultPhase = LifecyclePhase.DEPLOY)
public class FleekMojo extends BaseMojo {

    private final AmazonS3 s3;
    {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();/*.withClientConfiguration(
            new ClientConfiguration()
                                    .withMaxErrorRetry(10)
                                    .withConnectionTimeout(10_1000)
                                    .withSocketTimeout(10_000)
                                    .withTcpKeepAlive(true));*/
        builder.setEndpointConfiguration(
                new EndpointConfiguration("https://storageapi.fleek.co", Regions.US_EAST_1.getName()));
        builder.enablePathStyleAccess();
        s3 = builder.build();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

        runMavenDeploy(jfsDeployUrl);

        pushToBucket(jfsDeployDir, mavenProject.getArtifactId());
    }

    public Bucket getBucket(String bucket_name) {
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            log.info("Found bucket name " + b.getName());
        }
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    private void runMavenDeploy(String url) throws MojoExecutionException {
        log.info("JFS local deploy url: " + jfsDeployUrl);
        runCommandInteractive(mavenProject.getBasedir().getAbsolutePath(),
                "mvn install org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy -DaltDeploymentRepository=jfs-local-deploy::"
                        + url);
    }

    public void pushToBucket(String directory, String artifactId) throws MojoExecutionException {
        log.info("Deploying to Fleek...");

        String bucketName = "ericglau2-team-bucket";

        System.out.format("Getting S3 bucket: %s\n", bucketName);
        Bucket b = getBucket(bucketName);
        if (b == null) {
            log.info("Error getting bucket!\n");
        } else {
            log.info("Done!\n");
        }

       // s3.putObject(bucketName, "testkey", new File("/Users/eric/git/jfs/project-a/target/jfslocal/com/github/ericglau/project-a/maven-metadata.xml"));

       /*  TransferManager tm = new TransferManager(s3);
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
        MultipleFileUpload upload = tm.uploadDirectory(bucketName, "test1", new File(directory), true);
        try {
            upload.waitForCompletion();
        } catch (AmazonServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AmazonClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Uploaded! " + upload.toString()); */

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucketName,
                    "testkey", new File(directory), true);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();

        // String threadLinkLine = runCommand(directory, "hub buck init", "Enter a name for your new bucket", "",
        //         "already initialized", null, "Thread link");
        // runCommandInteractive(directory, "hub buck push -y");

        // log.debug("LINE = " + threadLinkLine);

        // String endString = threadLinkLine.substring(threadLinkLine.lastIndexOf("/") + 1);
        // log.debug("endString = " + endString);

        // String splitEnd = endString.split("[^a-zA-Z\\d]")[0];
        // log.debug("splitEnd = " + splitEnd);

        // int endOfSplitEndIndex = threadLinkLine.indexOf(splitEnd) + splitEnd.length();

        // String url = threadLinkLine.substring(threadLinkLine.indexOf("http"), endOfSplitEndIndex);

        // log.info("Bucket URL: " + url);

        // try {
        //     log.info(getFinalURL(new URL(url)).toString());
        // } catch (MalformedURLException e) {
        //     e.printStackTrace();
        // }

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