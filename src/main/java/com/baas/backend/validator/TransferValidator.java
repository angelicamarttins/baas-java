package com.baas.backend.validator;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.service.RegisterService;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TransferValidator {

  private final RegisterService registerService;

  public CustomerDto.Response verifyCustomerRegister(UUID customerId) throws ExecutionException, InterruptedException {
    log.info("Searching customer. CustomerId: {}", customerId);

    return registerService.findCustomer(customerId).thenApply(response -> {
      // Processar a resposta, se necessário
      return response;
    }).get();
  }

}
