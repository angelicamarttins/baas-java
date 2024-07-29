package com.baas.backend.service;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.exception.AccountNotFoundException;
import com.baas.backend.exception.UnavailableExternalServiceException;
import com.baas.backend.httpclient.AccountClient;
import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import com.baas.backend.repository.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

  private final TransferRepository transferRepository;
  private final AccountClient accountClient;
  private final ObjectMapper objectMapper;

  @SneakyThrows
  public AccountDto.Response findAccount(UUID transferId, UUID accountId) {
    return accountClient.getAccount(accountId)
      .exceptionally(exception -> {
        log.error("Account not found. AccountId: {}", accountId);
        transferRepository.updateTransferStatus(transferId, TransferStatus.FAILURE);

        throw new AccountNotFoundException(accountId);
      })
      .get();
  }

  @SneakyThrows
  public void updateAccountBalance(Transfer transfer) {
    TransferDataDto.Request transferDataRequest = new TransferDataDto.Request(
      transfer.getValue(),
      new TransferDto.TransferAccounts(
        transfer.getSourceAccountId(),
        transfer.getTargetAccountId()
      )
    );
    String transferBody = objectMapper.writeValueAsString(transferDataRequest);

    accountClient.updateAccountBalance(transferBody)
      .exceptionally(exception -> {
        log.error("External service for update balance is unavailable");
        transferRepository.updateTransferStatus(transfer.getTransferId(), TransferStatus.FAILURE);

        throw new UnavailableExternalServiceException(
          "Serviço externo de atualização do saldo está indisponível",
          HttpStatus.SERVICE_UNAVAILABLE,
          exception
        );
      })
      .get();

    transferRepository.updateTransferAfterBalance(transfer.getTransferId(), LocalDateTime.now());
  }

}
