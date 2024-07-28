package com.baas.backend.validator;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.model.Transfer;
import com.baas.backend.service.RedisService;
import com.baas.backend.service.RegisterService;
import java.time.Duration;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TransferValidator {

  private final RedisService redisService;
  private final RegisterService registerService;

  public CustomerDto.Response verifyTargetRegister(Transfer transfer) {
    log.info(
      "Searching customer. TransferId: {}, TargetId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transfer.getTargetId(),
      transfer.getTargetId(),
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
    );

    CustomerDto.Response cachedCustomer = (CustomerDto.Response) redisService.get(transfer.getTargetId().toString());

    if (Objects.isNull(cachedCustomer)) {
      CustomerDto.Response customer = registerService.findCustomer(transfer.getTargetId());
      redisService.set(transfer.getTargetId().toString(), customer, Duration.ofMinutes(30));

      return customer;
    }

    return cachedCustomer;
  }

}
