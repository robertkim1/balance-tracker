package com.pikel.balancetracker.balance.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    /**
     * Find all transactions for a specific user
     */
    List<TransactionEntity> findByUserId(UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}