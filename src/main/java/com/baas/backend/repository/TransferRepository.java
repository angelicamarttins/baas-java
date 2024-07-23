package com.baas.backend.repository;

import com.baas.backend.model.Transfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
