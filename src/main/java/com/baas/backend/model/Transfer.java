package com.baas.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfer")
public class Transfer {

  @Id
  @Column(nullable = false)
  @Builder.Default
  private UUID transferId = UUID.randomUUID();

  @Column(nullable = false)
  private UUID sourceAccountId;

  @Column(nullable = false)
  private UUID targetId;

  @Column(nullable = false)
  private UUID targetAccountId;

  @Column(nullable = false)
  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private TransferStatus status = TransferStatus.PROCESSING;

  @Column(nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column()
  private LocalDateTime balanceUpdatedAt;

  @Column()
  private LocalDateTime bacenUpdatedAt;

}
