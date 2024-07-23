package com.baas.backend.controller;

import com.baas.backend.data.dto.TransferRequestDto;
import com.baas.backend.data.dto.TransferResponseDto;
import com.baas.backend.service.TransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/transfer")
@RestController
@Slf4j
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  public ResponseEntity<TransferResponseDto> transfer(@RequestBody TransferRequestDto transferRequest) {
    log.info(
      "Starting transfer amount between accounts. ClientId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.clientId(),
      transferRequest.accounts().sourceAccountId(),
      transferRequest.accounts().targetAccountId()
    );

    return ResponseEntity.ok(transferService.saveTransfer(transferRequest));
  }

}
