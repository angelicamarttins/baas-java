package com.baas.backend.service;

import com.baas.backend.data.dto.TransferRequestDto;
import com.baas.backend.data.dto.TransferResponseDto;
import com.baas.backend.model.Transfer;
import com.baas.backend.repository.TransferRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferService {

  private final TransferRepository transferRepository;

  public TransferResponseDto saveTransfer(TransferRequestDto transferRequest) {
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

    transferRepository.save(transfer);

    return new TransferResponseDto(UUID.randomUUID());
  }

}
