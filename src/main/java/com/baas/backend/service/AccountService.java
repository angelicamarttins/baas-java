package com.baas.backend.service;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.exception.AccountNotFoundException;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.httpclient.AccountClient;
import com.baas.backend.model.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

  private final AccountClient accountClient;
  private final ObjectMapper objectMapper;

  public AccountDto.Response findAccount(UUID accountId) {
    try {
      return accountClient.getAccount(accountId);
    } catch (Exception exception) {
      log.error("Account not found. AccountId: {}", accountId);

      throw new AccountNotFoundException(accountId);
    }
  }

  public void updateAccountBalance(Transfer transfer) {
    try {
      TransferDataDto.Request transferDataRequest = new TransferDataDto.Request(
        transfer.getValue(),
        new TransferDto.TransferAccounts(
          transfer.getSourceAccountId(),
          transfer.getTargetAccountId()
        )
      );
      String transferBody = objectMapper.writeValueAsString(transferDataRequest);
      accountClient.updateAccountBalance(transferBody);
      transfer.setBalanceUpdatedAt(LocalDateTime.now());
    } catch (Exception exception) {
      String message = "External service for update balance is unavailable";
      log.error(message);

      throw new UnavailableExternalServiceException(message, exception);
    }
  }

}
