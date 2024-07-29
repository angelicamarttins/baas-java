package com.baas.backend.provider;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.model.AccountType;
import java.util.UUID;

public class CustomerDtoProvider {

  public static CustomerDto.Response createCustomerResponse(UUID customerId, AccountType accountType) {
    return new CustomerDto.Response(
      customerId,
      "Random Name",
      "11123456987",
      accountType
    );
  }

}
