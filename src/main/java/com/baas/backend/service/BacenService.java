package com.baas.backend.service;

import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.httpclient.BacenClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BacenService {

  private final BacenClient bacenClient;
  private final ObjectMapper objectMapper;

  public void notifyBacenSuccessfulTransfer(TransferDataDto.Request transferData) {
    try {
      String transferBody = objectMapper.writeValueAsString(transferData);
      bacenClient.notifyBacenSuccessfulTransfer(transferBody);
    } catch (Exception exception) {
      String message = "External service to notify Bacen is unavailable";
      log.error(message);

      throw new UnavailableExternalServiceException(message, exception);
    }
  }

}
