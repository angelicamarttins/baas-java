package com.baas.backend.service;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.builder.TransferBuilder;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import com.baas.backend.validator.TransferValidator;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferService {

  private final BacenService bacenService;
  private final TransferRepository transferRepository;
  private final TransferValidator transferValidator;
  private final Map<String, TransferStrategy> strategies;

  public TransferService(
    BacenService bacenService,
    TransferRepository transferRepository,
    TransferValidator transferValidator,
    StrategyValidator strategyValidator
  ) {
    this.bacenService = bacenService;
    this.transferRepository = transferRepository;
    this.transferValidator = transferValidator;
    this.strategies = strategyValidator.getStrategies();
  }

  public TransferDto.Response processTransfer(TransferDto.Request transferRequest) {
    log.info(
      "Starting transfer between accounts. CustomerId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.targetId(),
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );
    Transfer transfer = saveTransfer(transferRequest);

    CustomerDto.Response targetCustomer = transferValidator.verifyTargetRegister(transfer);
    TransferStrategy transferStrategy = strategies.get(targetCustomer.accountType().name());

    if (Objects.isNull(transferStrategy)) {
      log.info("Strategy with type {} is unavailable.", targetCustomer.accountType());
      throw new IllegalStateException("Strategy not found for " + targetCustomer.accountType());
    }

    AccountsVo accounts = transferStrategy.verifyAccounts(
      transfer.getTransferId(),
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    transferStrategy.verifyBalance(transfer.getTransferId(), accounts.sourceAccount(), transferRequest.value());
    transferStrategy.notifyBalanceService(transfer);
    bacenService.notifyBacenService(transfer, true);

    log.info(
      "Transfer occurred successfully. TransferId: {}, CustomerId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transfer.getTransferId(),
      transferRequest.targetId(),
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    return new TransferDto.Response(transfer.getTransferId());
  }

  private Transfer saveTransfer(TransferDto.Request transferRequest) {
    log.info(
      "Saving transfer. SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    Transfer transfer = TransferBuilder.buildNewTransfer(transferRequest);
    transferRepository.save(transfer);

    return transfer;
  }

}
