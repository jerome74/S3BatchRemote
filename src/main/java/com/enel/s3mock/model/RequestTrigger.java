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
public class RequestTrigger
{
    @JsonProperty("version")
    private String version;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("extractor")
    private String extractor;

    @JsonProperty("sparkEntity")
    private String sparkEntity;

    @JsonProperty("applicationFilter")
    private String applicationFilter;

    @JsonProperty("entity")
    private String entity;
}
