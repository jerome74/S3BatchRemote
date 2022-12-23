package com.enel.s3mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Entity {
    private String name;
    private String msName;
    private String msNumber;
    private String endpoint;
    private String version;
    private String extractor;
    private String sparkEntity;



}
