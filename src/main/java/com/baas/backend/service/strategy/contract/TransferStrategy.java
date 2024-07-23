package com.baas.backend.service.strategy.contract;

import com.baas.backend.model.Transfer;
import java.util.UUID;

public interface TransferStrategy {

  Object verifyClientRegistry(UUID clientId);

  Object verifyAccounts(UUID sourceAccountId, UUID targetAccountId);

  Object notifyBacen(Transfer transfer);

}
