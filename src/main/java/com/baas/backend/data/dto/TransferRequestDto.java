package com.baas.backend.data.dto;

import com.baas.backend.model.Accounts;
import java.math.BigDecimal;

public record TransferRequestDto(String clientId, BigDecimal value, Accounts accounts) {
}
