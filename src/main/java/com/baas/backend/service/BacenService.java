package com.baas.backend.service;

import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.httpclient.BacenClient;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import com.baas.backend.repository.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BacenService {

  private final TransferRepository transferRepository;
  private final BacenClient bacenClient;
  private final ObjectMapper objectMapper;

  public void notifyBacenService(Transfer transfer) {
    log.info(
      "Notifying Bacen external service. SourceAccountId: {}, TargetAccountId: {}",
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
    );

    TransferDataDto.Request transferDataRequest = new TransferDataDto.Request(
      transfer.getValue(),
      new TransferDto.TransferAccounts(
        transfer.getSourceAccountId(),
        transfer.getTargetAccountId()
      )
    );

    try {
      String transferBody = objectMapper.writeValueAsString(transferDataRequest);
      bacenClient.notifyBacenSuccessfulTransfer(transferBody);

      transfer.setBacenUpdatedAt(LocalDateTime.now());
      transfer.setStatus(TransferStatus.SUCCESS);
      transferRepository.save(transfer);
    } catch (Exception exception) {
      log.error("External service to notify Bacen is unavailable");
      // publicar na dlt
    }
  }

}
