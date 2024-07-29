package com.baas.backend.provider;

import com.baas.backend.data.dto.AccountDto;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountDtoProvider {

  public static AccountDto.Response createAccountResponse(UUID accountId, Boolean activeAccount) {
    return new AccountDto.Response(
      accountId,
      BigDecimal.TEN,
      activeAccount,
      BigDecimal.TWO
    );
  }

}
