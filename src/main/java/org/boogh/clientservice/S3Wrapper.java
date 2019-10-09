package org.boogh.clientservice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.boogh.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class S3Wrapper {

    private final ApplicationProperties.Aws aws;
    private final AWSCredentials credentials;
    private AmazonS3 s3client;

    private static final String defaultDirectory = "boogh-media/";
    private final Logger log = LoggerFactory.getLogger(S3Wrapper.class);

    public S3Wrapper(ApplicationProperties applicationProperties){
        this.aws = applicationProperties.getAws();
        this.credentials = new BasicAWSCredentials(aws.getBackendAccessKeyId(), aws.getBackendSecretAccessKey());
        s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();
    }

    public void uploadImages(List<File> images){
        for (File image: images) {
            s3client.putObject(aws.getBackendS3BucketName(), defaultDirectory + image.getName(), image);
        }
    }

    public List<String> listObjects() {
        List<String> imageNames = new ArrayList<>();

        ObjectListing objectListing = s3client.listObjects(aws.getBackendS3BucketName());
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            imageNames.add(os.getKey());
        }
        return imageNames;
    }

    public List<String> findReportImageNames(List<String> allImageNames, Long reportId){
        List<String> imageNames = new ArrayList<>();

        for (int i = 0; i < allImageNames.size(); i++) {
            String imageName = allImageNames.get(i);
            if (imageName.contains(reportId.toString())) {
                imageNames.add(imageName);
            }
        }
        return imageNames;
    }

    public void deleteObject(String key){
        s3client.deleteObject(aws.getBackendS3BucketName(), key);
    }
}
