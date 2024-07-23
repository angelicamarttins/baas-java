package com.baas.backend.service.strategy;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.httpclient.AccountClient;
import com.baas.backend.model.Account;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.contract.StrategyType;
import com.baas.backend.service.strategy.contract.TransferStrategy;
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
  private final AccountClient accountClient;

  @Override
  public void verifyAccounts(UUID sourceAccountId, UUID targetAccountId) {
    Account sourceAccount = accountClient.getAccount(sourceAccountId);
    Account targetAccount = accountClient.getAccount(targetAccountId);

    log.info("source = {}", sourceAccount);
    log.info("target = {}", targetAccount);
  }

  @Override
  public Transfer saveTransfer(TransferDto.Request transferRequest) {
    Transfer transfer = new Transfer(
      UUID.randomUUID(),
      transferRequest.customerId(),
      transferRequest.accounts().sourceAccountId(),
      UUID.randomUUID(),
      transferRequest.accounts().targetAccountId(),
      transferRequest.value(),
      LocalDateTime.now(),
      null
    );

    transferRepository.save(transfer);

    return transfer;
  }

  @Override
  public void notifyBacen(Transfer transfer) {
  }

}
