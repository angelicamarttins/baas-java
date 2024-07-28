package com.baas.backend.service;

import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.event.producer.ReprocessBacenProducer;
import com.baas.backend.httpclient.BacenClient;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import com.baas.backend.repository.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class BacenService {

  private final BacenClient bacenClient;
  private final ObjectMapper objectMapper;
  private final RedisService redisService;
  private final ReprocessBacenProducer reprocessBacenProducer;
  private final TransferRepository transferRepository;

  @SneakyThrows
  @Transactional
  public void notifyBacenService(Transfer transfer, boolean reprocessTransfer) {
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

    String transferBody = objectMapper.writeValueAsString(transferDataRequest);
    TransferDataDto.Response response = bacenClient.notifyBacenSuccessfulTransfer(transferBody)
      .exceptionally(exception -> {
        log.error("External service to notify Bacen is unavailable");

        if (reprocessTransfer) {
          redisService.set(transfer.getTransferId().toString(), transfer, Duration.ofMinutes(30));
          reprocessBacenProducer.publish(transfer);
        }
        return null;
      })
      .get();

    if (response != null && !HttpStatus.valueOf(response.code()).isError()) {
      transferRepository.updateTransferAfterBacenNotification(
        transfer.getTransferId(),
        LocalDateTime.now(),
        TransferStatus.SUCCESS
      );
    }
  }

}
