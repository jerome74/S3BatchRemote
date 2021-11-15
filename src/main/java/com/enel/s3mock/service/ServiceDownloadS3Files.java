package com.enel.s3mock.service;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface ServiceDownloadS3Files {

    Regions clientRegion = Regions.EU_CENTRAL_1;

    Path downloadS3Files(BasicSessionCredentials sessionCredentials, Path savePath, String prefixField, String bucketName) throws IOException;
}
