package com.enel.s3mock.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

public class ServiceDownloadS3FilesImpl  implements ServiceDownloadS3Files{

    private static final Logger log = LoggerFactory.getLogger(ServiceDownloadS3FilesImpl.class);

    @Override
    public Path downloadS3Files(BasicSessionCredentials sessionCredentials, Path savePath, String prefixField, String bucketName) throws IOException {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .withRegion(clientRegion)
                .build();


        log.info("Listing objects");

        ListObjectsV2Request objectsV2Request = (new ListObjectsV2Request()).withPrefix(prefixField + "/").withDelimiter("/").withBucketName(bucketName);

        ListObjectsV2Result result = s3Client.listObjectsV2(objectsV2Request);

        log.info("Result with Object Summaries is Empty => {}", String.valueOf(result.getObjectSummaries().isEmpty()));
        log.info("Result with Object Summaries size => {}", String.valueOf(result.getObjectSummaries().size()));
        log.info("Result with Common Prefixes  is Empty => {}", String.valueOf(result.getCommonPrefixes().isEmpty()));
        log.info("Result with Common Prefixes  size => {}", String.valueOf(result.getCommonPrefixes().size()));

        result.getCommonPrefixes().forEach(prefix ->  log.info("[ S3 ] prefix - {}", prefix) );


        return Optional.of(Files.createDirectories(savePath)).map(path -> {

            log.info("path => {}", path.toString());

            result.getObjectSummaries().forEach( objectSummary -> {
                log.info("[ S3 ] - {} (size: {}) (last modified: {})\n", objectSummary.getKey(), objectSummary.getSize(), objectSummary.getLastModified());
                try {
                    S3Object object = s3Client.getObject(new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));
                    InputStream objectData = object.getObjectContent();

                    var file = new File(path.toString() + "/"
                            + UUID.randomUUID().toString() + ".parquet").toPath();

                    Files.copy(objectData, file, StandardCopyOption.REPLACE_EXISTING);
                    // Process the objectData stream.
                    objectData.close();
                    log.info("[ S3 ] - copy file: {})\n", path);
                } catch (Exception ex) {
                    log.info("[ S3 ] - error to copy file : {})\n", ex.getMessage());
                }
            });
            return path;
        }).orElse(null);
    }
}
