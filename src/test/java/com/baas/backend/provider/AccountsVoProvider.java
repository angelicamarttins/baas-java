package com.baas.backend.provider;

import com.baas.backend.data.vo.AccountsVo;
import java.util.UUID;

public class AccountsVoProvider {

  public static AccountsVo createAccountsVo(UUID sourceId, UUID targetId) {
    return new AccountsVo(
      AccountDtoProvider.createAccountResponse(sourceId, true),
      AccountDtoProvider.createAccountResponse(targetId, true)
    );
  }

}
