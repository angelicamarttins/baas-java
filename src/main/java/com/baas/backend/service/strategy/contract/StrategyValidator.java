package com.baas.backend.service.strategy.contract;

import com.baas.backend.model.AccountType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class StrategyValidator {

  private final Map<String, TransferStrategy> strategies = new HashMap<>();

  public StrategyValidator(List<TransferStrategy> strategyList) {
    strategyList.forEach(strategy -> {
      StrategyType annotation = strategy.getClass().getAnnotation(StrategyType.class);
      if (Objects.nonNull(annotation)) {
        if (strategies.containsKey(annotation.value().name())) {
          throw new IllegalStateException(
            "Conflict detected! More than one strategy associated "
              + "with the following AccountType: " + annotation.value().name()
          );
        }
        strategies.put(annotation.value().name(), strategy);
      }
    });
  }
}
