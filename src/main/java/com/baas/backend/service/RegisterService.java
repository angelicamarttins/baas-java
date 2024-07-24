package com.baas.backend.service;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.exception.CustomerNotFoundException;
import com.baas.backend.httpclient.RegisterClient;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RegisterService {

  private final RegisterClient registerClient;

  public CustomerDto.Response findCustomer(UUID customerId) {
    try {
      return registerClient.getCustomer(customerId);
    } catch (Exception exception) {
      log.error("Customer not found. CustomerId: {}", customerId);

      throw new CustomerNotFoundException(customerId);
    }
  }

}
