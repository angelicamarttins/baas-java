package com.baas.backend.data.dto;

import java.math.BigDecimal;

public class TransferDataDto {

  public record Request(BigDecimal value, TransferDto.TransferAccounts accounts) {
  }

  public record Response(String body, int code) {
  }

}
