package com.baas.backend.service;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import com.baas.backend.validator.TransferValidator;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    CustomerDto.Response customer = transferValidator.verifyCustomerRegister(transferRequest.customerId());
    //    AccountType accountType = switch (customer.accountType()) {
    //      case Fisica:
    //        yield AccountType.NATURAL_PERSON;
    //      case Juridica:
    //        yield AccountType.LEGAL_PERSON;
    //    };
    //    log.info("accountType = {}", accountType);
    TransferStrategy transferStrategy = strategies.get(AccountType.NATURAL_PERSON);
    log.info("strategies = {}", strategies);
    log.info("CUSTOMER = {}", customer);

    if (Objects.isNull(transferStrategy)) {
      log.info("Strategy with type {} is unavailable.", customer.accountType());
      throw new IllegalStateException("Strategy not found for " + customer.accountType());
    }

    AccountsVo accounts = transferStrategy.verifyAccounts(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    transferStrategy.verifyBalance(accounts.sourceAccount(), transferRequest.value());
    Transfer transfer = transferStrategy.saveTransfer(transferRequest);
    transferStrategy.notifyBalanceService(transferRequest);
    transferStrategy.notifyBacenService(transfer);

    return new TransferDto.Response(transfer.getTransferId());
  }

}
