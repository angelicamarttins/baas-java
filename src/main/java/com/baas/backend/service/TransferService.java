package com.baas.backend.service;

import com.baas.backend.data.dto.TransferRequestDto;
import com.baas.backend.data.dto.TransferResponseDto;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

  private final TransferRepository transferRepository;
  private final Map<AccountType, TransferStrategy> strategies;

  public TransferService(
    TransferRepository transferRepository,
    StrategyValidator strategyValidator
  ) {
    this.transferRepository = transferRepository;
    this.strategies = strategyValidator.getStrategies();
  }

  public TransferResponseDto saveTransfer(TransferRequestDto transferRequest) {
    TransferStrategy transferStrategy = strategies.get(AccountType.NATURAL_PERSON);

    transferStrategy.verifyClientRegistry(transferRequest.clientId());
    transferStrategy.verifyAccounts(
      transferRequest.accounts().sourceAccountId(),
      transferRequest.accounts().targetAccountId()
    );
    Transfer transfer = new Transfer(
      UUID.randomUUID(),
      transferRequest.clientId(),
      transferRequest.accounts().sourceAccountId(),
      UUID.randomUUID(),
      transferRequest.accounts().targetAccountId(),
      transferRequest.value(),
      LocalDateTime.now(),
      null
    );
    transferStrategy.notifyBacen(transfer);

    transferRepository.save(transfer);

    return new TransferResponseDto(UUID.randomUUID());
  }

}
