package com.baas.backend.service.strategy.contract;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.model.Transfer;
import java.math.BigDecimal;
import java.util.UUID;

public interface TransferStrategy {

  AccountsVo verifyAccounts(UUID sourceAccountId, UUID targetAccountId);

  void verifyBalance(AccountDto.Response sourceAccount, BigDecimal value);

  Transfer saveTransfer(TransferDto.Request transferRequest);

  void notifyBalanceService(TransferDto.Request transferRequest);

  void notifyBacenService(Transfer transfer);

}
