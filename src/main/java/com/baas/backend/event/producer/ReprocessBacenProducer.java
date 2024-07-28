package com.baas.backend.event.producer;

import com.baas.backend.event.dto.ReprocessedTransfer;
import com.baas.backend.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ReprocessBacenProducer {

  private final KafkaTemplate<String, ReprocessedTransfer> kafkaTemplate;

  public void publish(Transfer transfer) {
    log.info("Sending Transfer event to be reprocessed - Message: {} - Topic: {} - Key: {}",
             transfer, "reprocess-bacen", transfer.getTransferId()
    );

    kafkaTemplate.send(
      "reprocess-bacen",
      transfer.getTransferId().toString(),
      new ReprocessedTransfer(transfer.getTransferId())
    );
  }

}
