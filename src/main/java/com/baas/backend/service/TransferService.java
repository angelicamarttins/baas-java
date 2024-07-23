package com.baas.backend.service;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Customer;
import com.baas.backend.model.Transfer;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferService {

  private final TransferValidator transferValidator;
  private final Map<AccountType, TransferStrategy> strategies;

  public TransferService(TransferValidator transferValidator, StrategyValidator strategyValidator) {
    this.transferValidator = transferValidator;
    this.strategies = strategyValidator.getStrategies();
  }

  public TransferDto.Response processTransfer(TransferDto.Request transferRequest) {
    Customer customer = transferValidator.verifyCustomerRegister(transferRequest.customerId());

    TransferStrategy transferStrategy = strategies.get(customer.accountType());

    if (Objects.isNull(transferStrategy)) {
      log.info("Strategy with type {} is unavailable.", customer.accountType());
      throw new IllegalStateException("Strategy not found for " + customer.accountType().name());
    }

    transferStrategy.verifyAccounts(
      transferRequest.accounts().sourceAccountId(),
      transferRequest.accounts().targetAccountId()
    );

    Transfer transfer = transferStrategy.saveTransfer(transferRequest);
    transferStrategy.notifyBacen(transfer);

    return new TransferDto.Response(transfer.getTransferId());
  }

}
