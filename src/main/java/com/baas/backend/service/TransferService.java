package com.baas.backend.service;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.event.producer.NotifyTransferProducer;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.builder.TransferBuilder;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferService {

  private final BacenService bacenService;
  private final NotifyTransferProducer notifyTransferProducer;
  private final RedisService redisService;
  private final RegisterService registerService;
  private final TransferRepository transferRepository;
  private final Map<String, TransferStrategy> strategies;

  public TransferService(
    BacenService bacenService,
    NotifyTransferProducer notifyTransferProducer,
    RedisService redisService,
    RegisterService registerService,
    TransferRepository transferRepository,
    StrategyValidator strategyValidator
  ) {
    this.bacenService = bacenService;
    this.notifyTransferProducer = notifyTransferProducer;
    this.redisService = redisService;
    this.registerService = registerService;
    this.transferRepository = transferRepository;
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

    CustomerDto.Response targetCustomer = verifyTargetRegister(transfer);
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
    notifyTransferProducer.publish(transfer);

    log.info(
      "Transfer occurred successfully. TransferId: {}, TargetId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transfer.getTransferId(),
      transfer.getTargetId(),
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
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

  private CustomerDto.Response verifyTargetRegister(Transfer transfer) {
    log.info(
      "Searching customer. TransferId: {}, TargetId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transfer.getTargetId(),
      transfer.getTargetId(),
      transfer.getSourceAccountId(),
      transfer.getTargetAccountId()
    );

    CustomerDto.Response cachedCustomer = (CustomerDto.Response) redisService.get(transfer.getTargetId().toString());

    if (Objects.isNull(cachedCustomer)) {
      CustomerDto.Response customer = registerService.findCustomer(transfer.getTargetId());
      redisService.set(transfer.getTargetId().toString(), customer, Duration.ofMinutes(30));

      return customer;
    }

    return cachedCustomer;
  }

}
