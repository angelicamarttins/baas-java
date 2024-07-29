package com.baas.backend.event.producer;

import com.baas.backend.config.properties.KafkaProperties;
import com.baas.backend.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotifyTransferProducer {

  private final KafkaProperties kafkaProperties;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void publish(Transfer transfer) {
    log.info(
      "Sending Transfer event to notify customers - Message: {} - Topic: {} - Key: {}",
      transfer, kafkaProperties.getReprocessBacenTopic(), transfer.getTransferId()
    );

    kafkaTemplate.send(
      kafkaProperties.getNotifyTransfer(),
      transfer.getTransferId().toString(),
      transfer.getTransferId().toString()
    );
  }

}
