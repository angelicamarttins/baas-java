package com.baas.backend.controller;

import com.baas.backend.data.dto.TransferRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@Slf4j
public class TransferController {

  @PostMapping
  public ResponseEntity<String> transfer(@RequestBody TransferRequestDto transferRequest) {
    log.info(
      "Starting transfer amount between accounts. ClientId: {}, SourceAccountId: {}, TargetAccountId: {}",
      transferRequest.clientId(),
      transferRequest.accounts().sourceAccountId(),
      transferRequest.accounts().targetAccountId()
    );

    return ResponseEntity.ok("Hello, world");
  }

}
