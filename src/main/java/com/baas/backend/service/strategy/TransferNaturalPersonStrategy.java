package com.baas.backend.service.strategy;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.exception.InactiveAccountException;
import com.baas.backend.exception.InsufficientBalanceException;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.AccountService;
import com.baas.backend.service.BacenService;
import com.baas.backend.service.strategy.contract.StrategyType;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@StrategyType(AccountType.NATURAL_PERSON)
public class TransferNaturalPersonStrategy implements TransferStrategy {

  private final TransferRepository transferRepository;
  private final AccountService accountService;
  private final BacenService bacenService;

  @Override
  public AccountsVo verifyAccounts(UUID sourceAccountId, UUID targetAccountId) {
    log.info(
      "Checking accounts. SourceAccountId: {}, TargetAccountId: {}",
      sourceAccountId,
      targetAccountId
    );

    AccountDto.Response sourceAccount = accountService.findAccount(sourceAccountId);
    AccountDto.Response targetAccount = accountService.findAccount(targetAccountId);

    if (!sourceAccount.activeAccount() || !targetAccount.activeAccount()) {
      throw new InactiveAccountException("The source or target account is inactive");
    }

    return new AccountsVo(sourceAccount, targetAccount);
  }

  @Override
  public void verifyBalance(AccountDto.Response sourceAccount, BigDecimal value) {
    log.info("Checking balance. SourceAccountId: {}", sourceAccount.accountId());

    BigDecimal balance = sourceAccount.balance().add(sourceAccount.dailyLimit());

    if (balance.compareTo(value) < 0) {
      throw new InsufficientBalanceException(value, balance);
    }
  }

  @Override
  public void notifyBalanceService(Transfer transfer) {
    log.info(
      "Notifying balance external service. SourceAccountId: {}, TargetAccountId: {}",
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
    );

    accountService.updateAccountBalance(transfer);
    transfer.setBalanceUpdatedAt(LocalDateTime.now());
    transferRepository.save(transfer);
  }

  @Override
  public void notifyBacenService(Transfer transfer) {
    log.info(
      "Notifying Bacen external service. SourceAccountId: {}, TargetAccountId: {}",
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
    );

    TransferDataDto.Request transferDataRequest = new TransferDataDto.Request(
      transfer.getValue(),
      new TransferDto.TransferAccounts(
        transfer.getSourceAccountId(),
        transfer.getTargetAccountId()
      )
    );

    bacenService.notifyBacenSuccessfulTransfer(transferDataRequest);
    transfer.setBacenUpdatedAt(LocalDateTime.now());
    transfer.setStatus(TransferStatus.SUCCESS);
    transferRepository.save(transfer);
  }
}
