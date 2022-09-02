package com.enel.s3mock.service;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface ServiceDownloadS3Files {

    Regions clientRegion = Regions.EU_CENTRAL_1;

    Path downloadS3Files(AmazonS3 s3Client, S3ObjectSummary objectSummary, Path savePath, String filename, int rows, StringBuffer buffer,  JTable tableData) throws IOException;
}
