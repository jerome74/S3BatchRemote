package com.enel.s3mock.unit;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.enel.s3mock.model.ResponseToken;
import com.enel.s3mock.model.ResponseWriteExecutionPlan;
import com.enel.s3mock.service.ServiceDownloadS3FilesImpl;
import com.enel.s3mock.util.PropertyParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Slf4j
public class S3ListingObjectsTest {



    @InjectMocks
    private ServiceDownloadS3FilesImpl serviceDownloadS3Files = new ServiceDownloadS3FilesImpl();

    private static final Regions clientRegion = Regions.EU_CENTRAL_1;
    private static final String PROPERTIES_FILE = "credentials.properties";

    @Test
    public void listingObjectsTest() {

        var prop = new PropertyParser();

        try {
            prop.load(S3ListingObjectsTest.class
                    .getClassLoader()
                    .getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebClient webClient = WebClient.create().mutate().build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", "RAP350891002");
        formData.add("password", "Dd3VTAM?z#tbvcpf");

        var writeExecutionPlanMono = webClient.post().uri(prop.getProperty("ms.path.token"))
                .header("Authorization", "Basic ".concat(prop.getProperty("basicauth.dev")))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve().bodyToMono(ResponseToken.class)
                .switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive token")))
                .flatMap(respToken -> {
                    return webClient
                            .post()
                            .uri(prop.getProperty("ms.path.writeexecutionplan"))
                            .header("Authorization", "Basic ".concat(respToken.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("{\n" +
                                    "    \"applicationFilter\": \"\",\n" +
                                    "    \"dataGovernanceFilter\": \"\",\n" +
                                    "    \"source\": \"External\"\n" +
                                    "}")
                            .retrieve()
                            .bodyToMono(ResponseWriteExecutionPlan.class)
                            .switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive WriteExecutionPlan")));

                });

        StepVerifier.create(writeExecutionPlanMono).consumeNextWith(writeExecutionPlan -> {

            var accessKeyID = writeExecutionPlan.getTemporaryCredentials().getRead().getAccessKeyID();
            var secretKey = writeExecutionPlan.getTemporaryCredentials().getRead().getSecretKey();
            var sessionToken = writeExecutionPlan.getTemporaryCredentials().getRead().getSessionToken();

            var bucketName = prop.getProperty("s3.bucketName.prefix").concat(prop.getProperty("s3.bucketName.suffix"));
            var prefixField = prop.getProperty("s3.prefixField");

            log.info("AccessKeyID => {}} \n", accessKeyID);
            log.info("SecretKey => {} \n", secretKey);
            log.info("SessionToken => {} \n", sessionToken);
            log.info("BucketName => {} \n", bucketName);
            log.info("Prefix => {} \n", prefixField);

            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                    accessKeyID,
                    secretKey,
                    sessionToken);


            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .withRegion(clientRegion)
                    .build();


            log.info("Listing objects");

            // ListObjectsV2Request objectsV2Request = (new ListObjectsV2Request()).withPrefix("compensatoritalym/v1/").withDelimiter("/").withBucketName("enel-dev-glin-ap31312mp01163-mecompensnetwbtch-cold-area");

            ListObjectsV2Request objectsV2Request = (new ListObjectsV2Request()).withPrefix(prefixField + "/").withDelimiter("/").withBucketName(bucketName);

            ListObjectsV2Result result = s3Client.listObjectsV2(objectsV2Request);

            log.info("Result with Object Summaries is Empty => {} \n", String.valueOf(result.getObjectSummaries().isEmpty()));
            log.info("Result with Object Summaries size => {} \n", String.valueOf(result.getObjectSummaries().size()));
            log.info("Result with Common Prefixes  is Empty => {} \n", String.valueOf(result.getCommonPrefixes().isEmpty()));
            log.info("Result with Common Prefixes  size => {} \n", String.valueOf(result.getCommonPrefixes().size()));

            var limit = Integer.parseInt(prop.getProperty("limit.file"));

            result.getCommonPrefixes().forEach(prefix -> log.info("[ MS ] prefix - {} \n", prefix));
            result.getObjectSummaries().subList(0, limit).forEach(objectSummary -> {
                log.info("[ MS ] - {} (size: {}}) (last modified: {})\n", objectSummary.getKey(), objectSummary.getSize(), objectSummary.getLastModified());

                S3Object object = s3Client.getObject(new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));
                InputStream objectData = object.getObjectContent();
                var filename = objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/") + 1);

                try {
                    serviceDownloadS3Files.downloadS3Files(s3Client
                            , result
                            , Paths.get("src", "test", "resources", prop.getProperty("s3.prefixField"))
                            , filename
                            ,Integer.parseInt(prop.getProperty("limit.row")));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            });

            Assertions.assertNotNull(result);

        }).verifyComplete();
    }
}
