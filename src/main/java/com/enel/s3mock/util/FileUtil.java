package com.enel.s3mock.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class FileUtil {

    public static void saveParquetAndPrintFile(AmazonS3 s3Client, ListObjectsV2Result result, Path savePath, String filename, int rows, StringBuffer buffer,JTable tableData) throws IOException {

        Optional.of(Files.createDirectories(savePath)).map(path -> {

            log.info("path => {}", path.toString());
            buffer.append("path => ").append(path.toString()).append(System.getProperty("line.separator"));

            result.getObjectSummaries().forEach(objectSummary -> {
                log.info("[ S3 ] - {} (size: {}) (last modified: {})\n", objectSummary.getKey(), objectSummary.getSize(), objectSummary.getLastModified());
                buffer.append("[ S3 ] - ").append(objectSummary.getKey())
                        .append(", size: ").append(objectSummary.getSize())
                        .append(", last modified: ").append(objectSummary.getLastModified()).append(System.getProperty("line.separator"));

                Path file = null;

                try {
                    S3Object object = s3Client.getObject(new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));
                    InputStream objectData = object.getObjectContent();

                    file = new File(path.toString() + "/"
                            + filename).toPath();

                    Files.copy(objectData, file, StandardCopyOption.REPLACE_EXISTING);
                    // Process the objectData stream.
                    objectData.close();

                   /* log.info("[ S3 ] - copy file: {})\n", path);
                    buffer.append("[ S3 ] - copy file: ").append(path).append(System.getProperty("line.separator"));*/

                } catch (Exception ex) {
                    log.info("[ S3 ] - error to copy file : {})\n", ex.getMessage());
                    buffer.append("[ S3 ] - error to copy file : ").append(ex.getMessage()).append(System.getProperty("line.separator"));
                }

                 /*
                    call ParquetUtil to print file
                     */
                assert file != null;
                ParquetUtil.printParquestFile(new org.apache.hadoop.fs.Path(file.toUri()),rows, buffer,tableData);


            });
            return null;
        });
    }
}
