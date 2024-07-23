package com.baas.backend.service;

import com.baas.backend.httpclient.RegisterClient;
import com.baas.backend.model.Customer;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransferValidator {

  private final RegisterClient registerClient;

  public Customer verifyCustomerRegister(UUID customerId) {
    return registerClient.getCustomer(customerId);
  }

}
