package com.baas.backend.model.builder;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.model.Transfer;

public class TransferBuilder {

  public static Transfer buildNewTransfer(TransferDto.Request transferRequest) {
    return Transfer
      .builder()
      .targetId(transferRequest.targetId())
      .sourceAccountId(transferRequest.transferAccounts().sourceAccountId())
      .targetAccountId(transferRequest.transferAccounts().targetAccountId())
      .value(transferRequest.value())
      .build();
  }

}
