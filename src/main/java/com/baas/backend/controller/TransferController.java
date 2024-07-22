package com.baas.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
public class TransferController {

  @PostMapping
  public ResponseEntity<String> transfer() {
    return ResponseEntity.ok("Hello, world");
  }

}
