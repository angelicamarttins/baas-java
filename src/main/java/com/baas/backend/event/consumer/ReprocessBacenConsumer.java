package com.baas.backend.event.consumer;

import com.baas.backend.event.dto.ReprocessedTransfer;
import com.baas.backend.exception.TransferNotFoundException;
import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.model.Transfer;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.BacenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ReprocessBacenConsumer {

  private final BacenService bacenService;
  private final TransferRepository transferRepository;

  @KafkaListener(
    topics = "${spring.kafka.topics.reprocess-bacen.name}",
    groupId = "${spring.kafka.topics.reprocess-bacen.group}"
  )
  public void reprocessTransferBacen(
    ConsumerRecord<String, ReprocessedTransfer> record,
    Acknowledgment ack
  ) {
    log.info(
      "Receiving ReprocessedTransfer event - Message: {} - Topic: {} - Key: {}",
      record.value(), record.topic(), record.key()
    );

    try {
      Transfer transfer = transferRepository
        .findById(record.value().getTransferId())
        .orElseThrow(() -> {
          ErrorData errorData = new ErrorData(
            "Transfer not found. TransferId: " + record.value().getTransferId(),
            HttpStatus.NOT_FOUND
          );
          return new TransferNotFoundException(errorData);
        });

      bacenService.notifyBacenService(transfer, false);
    } catch (Exception exception) {
      log.error(
        "Error while processing NotificationRequested event - Message: {} - Topic: {} - Key: {} - Error: {}",
        record.value(), record.topic(), record.key(), exception.getMessage(), exception
      );
    } finally {
      ack.acknowledge();
    }
  }

}
