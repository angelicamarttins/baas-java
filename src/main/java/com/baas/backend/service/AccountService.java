package com.baas.backend.service;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.exception.AccountNotFoundException;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.httpclient.AccountClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  public void updateAccountBalance(TransferDataDto.Request transferRequest) {
    try {
      String transferBody = objectMapper.writeValueAsString(transferRequest);
      accountClient.updateAccountBalance(transferBody);
    } catch (Exception exception) {
      String message = "External service for update balance is unavailable";
      log.error(message);

      throw new UnavailableExternalServiceException(message, exception);
    }
  }

}
