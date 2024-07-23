package com.baas.backend.service;

import com.baas.backend.data.dto.TransferRequestDto;
import com.baas.backend.data.dto.TransferResponseDto;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

  public TransferResponseDto saveTransfer(TransferRequestDto transferRequest) {
    return new TransferResponseDto(UUID.randomUUID());
  }

}
