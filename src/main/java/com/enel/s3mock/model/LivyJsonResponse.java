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
public class LivyJsonResponse {

    @JsonProperty("output")
    private OutputReponse output;

    @JsonProperty("from")
    private Integer from;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("total")
    public List<SessionLivy> sessions;


}
