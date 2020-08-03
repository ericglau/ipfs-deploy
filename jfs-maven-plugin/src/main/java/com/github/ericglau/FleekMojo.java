package com.github.ericglau;

import java.io.File;
import java.util.List;

import com.amazonaws.AmazonServiceException;
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
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "fleek", defaultPhase = LifecyclePhase.DEPLOY)
public class FleekMojo extends BaseMojo {

    /**
     * The Fleek bucket to upload to.
     */
    @Parameter(property = "bucket", required = true)
    private String bucket;

    /**
     * The directory in the Fleek bucket to use. Optional.
     */
    @Parameter(property = "directory", required = false)
    private String directory;

    private final AmazonS3 s3;
    {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
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

    public void pushToBucket(String localDirectory, String artifactId) throws MojoExecutionException {
        log.info("Deploying to Fleek...");

        String bucketName = bucket;

        log.info("Getting Fleek S3 bucket: " + bucketName);
        Bucket b = getBucket(bucketName);
        if (b == null) {
            log.info("Error getting bucket!");
        } else {
            log.info("Got bucket!");
        }

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucketName,
                    directory, new File(localDirectory), true);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            log.error("Could not transfer to Fleek", e);
        }
        xfer_mgr.shutdownNow();

        log.info("Successfully deployed to Fleek bucket:");
        log.info("https://" + bucket + ".storage.fleek.co/" + (directory == null ? "" : directory));
    }

}