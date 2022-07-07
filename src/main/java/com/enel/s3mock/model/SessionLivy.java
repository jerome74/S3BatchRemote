package com.enel.s3mock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SessionLivy {

    @JsonProperty("output")
    public int id;

    @JsonProperty("name")
    public Object name;

    @JsonProperty("state")
    public String state;

    @JsonProperty("appId")
    public String appId;

    @JsonProperty("appInfo")
    public AppInfo appInfo;

    @JsonProperty("log")
    public List<String> log;

}
