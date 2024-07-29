package com.baas.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.event.producer.NotifyTransferProducer;
import com.baas.backend.exception.AccountNotFoundException;
import com.baas.backend.exception.CustomerNotFoundException;
import com.baas.backend.exception.InsufficientBalanceException;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.model.AccountType;
import com.baas.backend.provider.AccountsVoProvider;
import com.baas.backend.provider.CustomerDtoProvider;
import com.baas.backend.provider.TransferDtoProvider;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.TransferNaturalPersonStrategy;
import com.baas.backend.service.strategy.contract.StrategyValidator;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransferServiceUnitTest {

  private TransferService transferService;

  @Mock
  private BacenService bacenService;

  @Mock
  private NotifyTransferProducer notifyTransferProducer;

  @Mock
  private RedisService redisService;

  @Mock
  private RegisterService registerService;

  @Mock
  private TransferRepository transferRepository;

  @Mock
  private StrategyValidator strategyValidator;

  private void initializeNotificationProcessorService() {
    transferService = new TransferService(
      bacenService,
      notifyTransferProducer,
      redisService,
      registerService,
      transferRepository,
      strategyValidator
    );
  }

  private void initWithStrategies(Map<String, TransferStrategy> strategiesMap) {
    when(strategyValidator.getStrategies()).thenReturn(strategiesMap);
    initializeNotificationProcessorService();
  }

  @Test
  @DisplayName("When customer is not found, throws exception correctly")
  void whenCustomerIsNotFound_shouldThrowsException() {
    initWithStrategies(new HashMap<>());
    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(null);

    when(registerService.findCustomer(any())).thenThrow(CustomerNotFoundException.class);

    Assertions.assertThrows(
      CustomerNotFoundException.class,
      () -> {
        transferService.processTransfer(transferRequest);
      }
    );
  }

  @Test
  @DisplayName("When no class implements TransferStrategy, throws exception correctly")
  void whenNoClassImplementsTransferStrategy_shouldThrowsException() {
    initWithStrategies(new HashMap<>());
    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(null);
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );

    when(redisService.get(any())).thenReturn(customer);

    Assertions.assertThrows(
      IllegalStateException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When accounts are not found, throws exception correctly")
  void whenAccountsAreNotFound_shouldThrowsException() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(null);
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenThrow(AccountNotFoundException.class);

    Assertions.assertThrows(
      AccountNotFoundException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When value is greater than balance or daily limit, throws exception correctly")
  void whenValueIsGreaterThanBalanceOrDailyLimit_shouldThrowsException() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.valueOf(15L));
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );
    AccountsVo accounts = AccountsVoProvider.createAccountsVo(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenReturn(accounts);
    doThrow(InsufficientBalanceException.class).when(transferStrategy).verifyBalance(any(), any(), any());

    Assertions.assertThrows(
      InsufficientBalanceException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When value is greater than balance, throws exception correctly")
  void whenValueIsGreaterThanBalance_shouldThrowsException() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.valueOf(15L));
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );
    AccountsVo accounts = AccountsVoProvider.createAccountsVo(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenReturn(accounts);
    doThrow(InsufficientBalanceException.class).when(transferStrategy).verifyBalance(any(), any(), any());

    Assertions.assertThrows(
      InsufficientBalanceException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When external balance service is unavailable, throws exception correctly")
  void whenExternalBalanceServiceIsUnavailable_shouldThrowsException() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.valueOf(15L));
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );
    AccountsVo accounts = AccountsVoProvider.createAccountsVo(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenReturn(accounts);
    doNothing().when(transferStrategy).verifyBalance(any(), any(), any());
    doThrow(UnavailableExternalServiceException.class).when(transferStrategy).notifyBalanceService(any());

    Assertions.assertThrows(
      UnavailableExternalServiceException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When external Bacen service is unavailable, throws exception correctly")
  void whenExternalBacenServiceIsUnavailable_shouldThrowsException() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.valueOf(15L));
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );
    AccountsVo accounts = AccountsVoProvider.createAccountsVo(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenReturn(accounts);
    doNothing().when(transferStrategy).verifyBalance(any(), any(), any());
    doNothing().when(transferStrategy).notifyBalanceService(any());
    doThrow(UnavailableExternalServiceException.class).when(bacenService).notifyBacenService(any(), eq(true));

    Assertions.assertThrows(
      UnavailableExternalServiceException.class,
      () -> transferService.processTransfer(transferRequest)
    );
  }

  @Test
  @DisplayName("When everything is ok, returns correctly")
  void whenEverythingIsOk_shouldReturnsCorrectly() {
    HashMap<String, TransferStrategy> strategies = new HashMap<>();
    TransferStrategy transferStrategy = mock(TransferNaturalPersonStrategy.class);
    strategies.put(AccountType.NATURAL_PERSON.name(), transferStrategy);
    initWithStrategies(strategies);

    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.valueOf(15L));
    CustomerDto.Response customer = CustomerDtoProvider.createCustomerResponse(
      transferRequest.targetId(),
      AccountType.NATURAL_PERSON
    );
    AccountsVo accounts = AccountsVoProvider.createAccountsVo(
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    when(redisService.get(transferRequest.targetId().toString())).thenReturn(customer);
    when(transferStrategy.verifyAccounts(any(), any(), any())).thenReturn(accounts);
    doNothing().when(transferStrategy).verifyBalance(any(), any(), any());
    doNothing().when(transferStrategy).notifyBalanceService(any());
    doNothing().when(bacenService).notifyBacenService(any(), eq(true));

    TransferDto.Response response = transferService.processTransfer(transferRequest);

    verify(notifyTransferProducer, atLeastOnce()).publish(any());
    Assertions.assertNotNull(response.transferId());
  }
}
