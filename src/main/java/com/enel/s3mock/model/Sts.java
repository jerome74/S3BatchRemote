package com.enel.s3mock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Sts {

    @JsonProperty("accessKeyID")
    private String accessKeyID;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("sessionToken")
    private String sessionToken;
}