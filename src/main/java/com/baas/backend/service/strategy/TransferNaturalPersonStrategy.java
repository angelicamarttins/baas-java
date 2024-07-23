package com.baas.backend.service.strategy;

import com.baas.backend.model.AccountType;
import com.baas.backend.model.Transfer;
import com.baas.backend.service.strategy.contract.StrategyType;
import com.baas.backend.service.strategy.contract.TransferStrategy;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@StrategyType(AccountType.NATURAL_PERSON)
public class TransferNaturalPersonStrategy implements TransferStrategy {


  @Override
  public Object verifyClientRegistry(UUID clientId) {
    return null;
  }

  @Override
  public Object verifyAccounts(UUID sourceAccountId, UUID targetAccountId) {
    return null;
  }

  @Override
  public Object notifyBacen(Transfer transfer) {
    return null;
  }
}
