package com.baas.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfer")
public class Transfer {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID transferId;

  @Column(nullable = false)
  private UUID clientSourceId;

  @Column(nullable = false)
  private UUID sourceAccountId;

  @Column(nullable = false)
  private UUID clientTargetId;

  @Column(nullable = false)
  private UUID targetAccountId;

  @Column(nullable = false)
  private BigDecimal value;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column()
  private LocalDateTime updatedAt;

  @PrePersist
  void onCreate() {
    this.setCreatedAt(LocalDateTime.now());
  }

  @PreUpdate
  void onUpdate() {
    this.setUpdatedAt(LocalDateTime.now());
  }

}
