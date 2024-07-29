package com.baas.backend.data.dto;

import com.baas.backend.exception.EqualAccountsException;
import com.baas.backend.exception.common.ErrorData;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class TransferDto {

  public record Request(
    @NotNull(message = "idCliente não deve ser nulo")
    @JsonProperty("idCliente")
    UUID targetId,

    @JsonProperty("valor")
    @NotNull(message = "valor não deve ser nulo")
    @Positive(message = "valor deve ser positivo")
    BigDecimal value,

    @NotNull(message = "conta não deve ser nulo")
    @JsonProperty("conta")
    TransferAccounts transferAccounts
  ) {
  }

  public record Response(@JsonProperty("id_transferencia") UUID transferId) {
  }

  public record TransferAccounts(
    @JsonProperty("idOrigem")
    @NotNull(message = "idOrigem não deve ser nulo")
    UUID sourceAccountId,

    @JsonProperty("idDestino")
    @NotNull(message = "idDestino não deve ser nulo")
    UUID targetAccountId
  ) {
    public TransferAccounts {
      if (sourceAccountId.equals(targetAccountId)) {
        ErrorData errorData = new ErrorData(
          "Conta origem e conta destino não devem ser iguais.",
          HttpStatus.BAD_REQUEST
        );

        throw new EqualAccountsException(errorData);
      }
    }
  }

}
