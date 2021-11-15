package com.enel.s3mock;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.enel.s3mock.service.ServiceDownloadS3FilesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
public class ServiceDownloadS3FilesTest {

    private static final Logger log = LoggerFactory.getLogger(ServiceDownloadS3FilesTest.class);

    private BasicSessionCredentials sessionCredentials;

    private String bucketName;

    private String prefix;

    private Path path;

    @InjectMocks
    private ServiceDownloadS3FilesImpl serviceDownloadS3Files;

    @BeforeEach
    public void setUp() {

        JTextField accessKeyID = new JTextField();
        accessKeyID.setSize(new Dimension(300, 16));

        JTextField secretKey = new JTextField();
        secretKey.setSize(new Dimension(300, 16));

        JTextField sessionToken = new JTextField();
        sessionToken.setSize(new Dimension(300, 16));

        JTextField bucketNameField = new JTextField();
        bucketNameField.setSize(new Dimension(300, 16));

        JTextField prefixField = new JTextField();
        prefixField.setSize(new Dimension(300, 16));

        Object[] inputCredentials = {
                "AccessKeyID:", accessKeyID,
                "SecretKey:", secretKey,
                "SessionToken:", sessionToken,
                "BucketName:", bucketNameField,
                "Prefix:", prefixField
        };


        int option = JOptionPane.showConfirmDialog(null, inputCredentials, "InputCredentials - S3", JOptionPane.DEFAULT_OPTION);
        if (option == JOptionPane.OK_OPTION) {

            log.info("AccessKeyID => {}", accessKeyID.getText());
            log.info("SecretKey => {}", secretKey.getText());
            log.info("SessionToken => {}", sessionToken.getText());
            log.info("BucketName => {}", bucketNameField.getText());
            log.info("Prefix => {}", prefixField.getText());


            sessionCredentials = new BasicSessionCredentials(
                    accessKeyID.getText(),
                    secretKey.getText(),
                    sessionToken.getText());

            bucketName = bucketNameField.getText();
            prefix = prefixField.getText();
            path = Paths.get("src","test","resources" , prefixField.getText());
        }

    }

    @Test
    @DisplayName("Given service S3, when secret access are provided, then return file parquet")
    public void downloadFile() throws IOException {

        Path downloadS3Files = serviceDownloadS3Files.downloadS3Files(sessionCredentials, this.path, prefix, bucketName);

        Assertions.assertNotEquals(Objects.requireNonNull(downloadS3Files.toFile().listFiles()).length, 0);

    }

}
