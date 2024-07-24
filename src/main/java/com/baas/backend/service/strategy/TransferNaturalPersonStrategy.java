package com.baas.backend.service.strategy;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.exception.InsufficientBalanceException;
import com.baas.backend.exception.InvalidTransferParametersException;
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
      throw new InvalidTransferParametersException("The source or destination account is inactive");
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
  public Transfer saveTransfer(TransferDto.Request transferRequest) {
    log.info(
      "Saving transfer. SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    Transfer transfer = new Transfer(
      UUID.randomUUID(),
      transferRequest.customerId(),
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId(),
      transferRequest.value(),
      TransferStatus.PROCESSING,
      LocalDateTime.now(),
      null,
      null
    );

    transferRepository.save(transfer);

    return transfer;
  }

  @Override
  public void notifyBalanceService(TransferDto.Request transferRequest) {
    log.info(
      "Notifying balance external service. SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    TransferDataDto.Request transferDataRequest = new TransferDataDto.Request(
      transferRequest.value(),
      transferRequest.transferAccounts()
    );

    accountService.updateAccountBalance(transferDataRequest);
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
  }
}
