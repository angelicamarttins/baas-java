package com.baas.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.data.vo.AccountsVo;
import com.baas.backend.exception.AccountNotFoundException;
import com.baas.backend.exception.InactiveAccountException;
import com.baas.backend.exception.InsufficientBalanceException;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.model.Transfer;
import com.baas.backend.provider.AccountDtoProvider;
import com.baas.backend.provider.TransferDtoProvider;
import com.baas.backend.provider.TransferProvider;
import com.baas.backend.repository.TransferRepository;
import com.baas.backend.service.strategy.TransferNaturalPersonStrategy;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransferNaturalPersonStrategyUnitTest {

  @InjectMocks
  private TransferNaturalPersonStrategy transferNaturalPersonStrategy;

  @Mock
  private TransferRepository transferRepository;

  @Mock
  private AccountService accountService;

  @Test
  @DisplayName("When verifyAccounts and any account is not found, throws exception correctly")
  void whenVerifyAccountsAndAnyAccountIsNotFound_shouldThrowsException() {
    AccountDto.Response account = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);

    when(accountService.findAccount(any(), any())).thenThrow(AccountNotFoundException.class);

    Assertions.assertThrows(
      AccountNotFoundException.class,
      () -> transferNaturalPersonStrategy.verifyAccounts(UUID.randomUUID(), account.accountId(), UUID.randomUUID())
    );
  }

  @Test
  @DisplayName("When verifyAccounts and target account is not active, throws exception correctly")
  void whenVerifyAccountsAndTargetAccountIsNotActive_shouldThrowsException() {
    AccountDto.Response sourceAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), false);
    AccountDto.Response targetAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);

    when(accountService.findAccount(any(), any())).thenReturn(sourceAccount).thenReturn(targetAccount);

    Assertions.assertThrows(
      InactiveAccountException.class,
      () -> transferNaturalPersonStrategy.verifyAccounts(
        UUID.randomUUID(),
        sourceAccount.accountId(),
        targetAccount.accountId()
      )
    );
  }

  @Test
  @DisplayName("When verifyAccounts and source account is not active, throws exception correctly")
  void whenVerifyAccountsAndSourceAccountIsNotActive_shouldThrowsException() {
    AccountDto.Response sourceAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);
    AccountDto.Response targetAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), false);

    when(accountService.findAccount(any(), any())).thenReturn(sourceAccount).thenReturn(targetAccount);

    Assertions.assertThrows(
      InactiveAccountException.class,
      () -> transferNaturalPersonStrategy.verifyAccounts(
        UUID.randomUUID(),
        sourceAccount.accountId(),
        targetAccount.accountId()
      )
    );
  }

  @Test
  @DisplayName("When verifyAccounts and everything is ok, returns correctly")
  void whenVerifyAccountsAndEverythingIsOk_shouldThrowsException() {
    AccountDto.Response sourceAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);
    AccountDto.Response targetAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);
    AccountsVo expectedAccounts = new AccountsVo(sourceAccount, targetAccount);

    when(accountService.findAccount(any(), any())).thenReturn(sourceAccount).thenReturn(targetAccount);

    AccountsVo actualAccounts = transferNaturalPersonStrategy.verifyAccounts(
      UUID.randomUUID(),
      sourceAccount.accountId(),
      targetAccount.accountId()
    );

    Assertions.assertEquals(actualAccounts, expectedAccounts);
  }

  @Test
  @DisplayName("When verifyBalance and value is greater than balance, throws exception correctly")
  void whenVerifyBalanceAndValueIsGreaterThanBalance_shouldThrowsException() {
    AccountDto.Response sourceAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);

    Assertions.assertThrows(
      InsufficientBalanceException.class,
      () -> transferNaturalPersonStrategy.verifyBalance(UUID.randomUUID(), sourceAccount, BigDecimal.valueOf(15L))
    );
  }

  @Test
  @DisplayName("When verifyBalance and value is greater than daily limit, throws exception correctly")
  void whenVerifyBalanceAndValueIsGreaterThanDailyLimit_shouldThrowsException() {
    AccountDto.Response sourceAccount = AccountDtoProvider.createAccountResponse(UUID.randomUUID(), true);

    Assertions.assertThrows(
      InsufficientBalanceException.class,
      () -> transferNaturalPersonStrategy.verifyBalance(UUID.randomUUID(), sourceAccount, BigDecimal.valueOf(5L))
    );
  }

  @Test
  @DisplayName("When notifyBalanceService and external service is unavailable, throws exception correctly")
  void whenNotifyBalanceServiceAndExternalServiceIsUnavailable_shouldThrowsException() {
    TransferDto.Request transferRequest = TransferDtoProvider.createTransferRequest(BigDecimal.TEN);
    Transfer transfer = TransferProvider.createTransfer(transferRequest);

    doThrow(UnavailableExternalServiceException.class).when(accountService).updateAccountBalance(any());

    Assertions.assertThrows(
      UnavailableExternalServiceException.class,
      () -> transferNaturalPersonStrategy.notifyBalanceService(transfer)
    );
  }
}
