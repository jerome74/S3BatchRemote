package com.enel.s3mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseAggrBsTrigger {

    private ResponseBsTrigger responseBsTrigger;

    private String emsJsonResponse;
}
