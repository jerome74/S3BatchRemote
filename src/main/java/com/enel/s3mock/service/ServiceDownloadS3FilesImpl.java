package com.enel.s3mock.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.enel.s3mock.util.ParquetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;


@Service
@Slf4j
public class ServiceDownloadS3FilesImpl  implements ServiceDownloadS3Files{

    private static final Logger log = LoggerFactory.getLogger(ServiceDownloadS3FilesImpl.class);

    public  Path downloadS3Files(AmazonS3 s3Client, ListObjectsV2Result result, Path savePath, String filename, int rows) throws IOException {

       return  Optional.of(Files.createDirectories(savePath)).map(path -> {

            log.info("path => {}", path.toString());

            result.getObjectSummaries().forEach(objectSummary -> {
                log.info("[ S3 ] - {} (size: {}) (last modified: {})\n", objectSummary.getKey(), objectSummary.getSize(), objectSummary.getLastModified());

                Path file = null;

                try {
                    S3Object object = s3Client.getObject(new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));
                    InputStream objectData = object.getObjectContent();

                    file = new File(path.toString() + "/"
                            + filename).toPath();

                    Files.copy(objectData, file, StandardCopyOption.REPLACE_EXISTING);
                    // Process the objectData stream.
                    objectData.close();

                    log.info("[ S3 ] - copy file: {})\n", path);
                } catch (Exception ex) {
                    log.info("[ S3 ] - error to copy file : {})\n", ex.getMessage());
                }

                 /*
                    call ParquetUtil to print file
                     */
                assert file != null;
                ParquetUtil.printParquestFile(new org.apache.hadoop.fs.Path(file.toUri()),rows);


            });
            return path;
        }).orElse(Path.of(""));
    }
}
