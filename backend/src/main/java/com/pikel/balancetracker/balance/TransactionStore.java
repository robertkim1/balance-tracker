package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.entity.TransactionEntity;
import com.pikel.balancetracker.balance.entity.TransactionEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionStore {

    private final TransactionEntityRepository repo;

    public TransactionStore(TransactionEntityRepository repo) {
        this.repo = repo;
    }

    public List<TransactionEntity> findByUserId(UUID userId) {
        return repo.findByUserId(userId);
    }

    public void deleteByIdAndUserId(UUID userId, UUID transactionId) {
        repo.deleteByIdAndUserId(transactionId, userId);
    }

    public void save(TransactionEntity entity) {
        repo.save(entity);
    }

}

