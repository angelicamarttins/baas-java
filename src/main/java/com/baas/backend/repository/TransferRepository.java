package com.baas.backend.repository;

import com.baas.backend.model.Transfer;
import com.baas.backend.model.TransferStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

  @Modifying
  @Transactional
  @Query(
    "UPDATE Transfer t "
      + "SET t.status = :status "
      + "WHERE t.transferId = :transferId")
  void updateTransferStatus(UUID transferId, TransferStatus status);

  @Modifying
  @Transactional
  @Query(
    "UPDATE Transfer t "
      + "SET t.balanceUpdatedAt = :balanceUpdatedAt "
      + "WHERE t.transferId = :transferId")
  void updateTransferAfterBalance(UUID transferId, LocalDateTime balanceUpdatedAt);

  @Modifying
  @Query(
    "UPDATE Transfer t "
      + "SET t.bacenUpdatedAt = :bacenUpdatedAt, t.status = :status "
      + "WHERE t.transferId = :transferId")
  void updateTransferAfterBacenNotification(
    UUID transferId,
    LocalDateTime bacenUpdatedAt,
    TransferStatus status
  );

}
