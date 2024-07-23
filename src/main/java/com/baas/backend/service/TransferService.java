package com.baas.backend.service;

import com.baas.backend.data.dto.TransferRequestDto;
import com.baas.backend.data.dto.TransferResponseDto;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TransferService {

  public TransferResponseDto saveTransfer(TransferRequestDto transferRequest) {
    return new TransferResponseDto(UUID.randomUUID());
  }

}
