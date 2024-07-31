package com.baas.backend.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaProperties {
  @Value("${spring.kafka.topics.reprocess-bacen.name}")
  private String reprocessBacenTopic;
}
