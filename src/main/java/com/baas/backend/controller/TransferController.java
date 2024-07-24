package com.baas.backend.controller;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.service.TransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/transfer")
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  public ResponseEntity<TransferDto.Response> transfer(@RequestBody TransferDto.Request transferRequest) {
    log.info(
      "Starting transfer between accounts. CustomerId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.customerId(),
      transferRequest.transferAccounts().sourceAccountId(),
      transferRequest.transferAccounts().targetAccountId()
    );

    return ResponseEntity.ok(transferService.processTransfer(transferRequest));
  }

}
