package com.baas.backend.provider;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.model.Transfer;

public class TransferProvider {

  public static Transfer createTransfer(TransferDto.Request transferRequest) {
    return Transfer
      .builder()
      .targetId(transferRequest.targetId())
      .sourceAccountId(transferRequest.transferAccounts().sourceAccountId())
      .targetAccountId(transferRequest.transferAccounts().targetAccountId())
      .value(transferRequest.value())
      .build();
  }

}
