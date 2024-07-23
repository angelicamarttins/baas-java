package com.baas.backend.service.strategy.contract;

import com.baas.backend.data.dto.TransferDto;
import com.baas.backend.model.Transfer;
import java.util.UUID;

public interface TransferStrategy {

  void verifyAccounts(UUID sourceAccountId, UUID targetAccountId);

  Transfer saveTransfer(TransferDto.Request transferRequest);

  void notifyBacen(Transfer transfer);

}
