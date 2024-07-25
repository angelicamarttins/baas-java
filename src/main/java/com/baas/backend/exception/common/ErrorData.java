package com.baas.backend.exception.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorData(
  @JsonProperty("errorReason") String errorReason,
  @JsonProperty("errorCode") HttpStatus errorCode
) {
}
