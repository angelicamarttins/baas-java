package com.baas.backend.service.strategy;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.exception.InactiveAccountException;
import com.baas.backend.exception.InsufficientBalanceException;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.AccountService;
import com.baas.backend.service.strategy.contract.StrategyType;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.math.BigDecimal;
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

  @Override
  public AccountsVo verifyAccounts(UUID transferId, UUID sourceAccountId, UUID targetAccountId) {
    log.info(
      "Checking accounts. SourceAccountId: {}, TargetAccountId: {}",
      sourceAccountId,
      targetAccountId
    );

    AccountDto.Response sourceAccount = accountService.findAccount(transferId, sourceAccountId);
    AccountDto.Response targetAccount = accountService.findAccount(transferId, targetAccountId);

    if (!sourceAccount.activeAccount() || !targetAccount.activeAccount()) {
      transferRepository.updateTransferStatus(transferId, TransferStatus.FAILURE);

      throw new InactiveAccountException("A conta de origem ou destino está inativa");
    }

    return new AccountsVo(sourceAccount, targetAccount);
  }

  @Override
  public void verifyBalance(UUID transferId, AccountDto.Response sourceAccount, BigDecimal value) {
    log.info("Checking balance. SourceAccountId: {}", sourceAccount.accountId());
    boolean hasBalance = value.compareTo(sourceAccount.balance()) <= 0
      && value.compareTo(sourceAccount.dailyLimit()) <= 0;

    if (!hasBalance) {
      transferRepository.updateTransferStatus(transferId, TransferStatus.FAILURE);

      throw new InsufficientBalanceException();
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
  }

}
