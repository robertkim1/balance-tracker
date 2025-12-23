package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.entity.TransactionEntity;
import com.pikel.balancetracker.exception.BalanceTrackerException;
import com.pikel.balancetracker.balance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerService.class);

    private final TransactionStore transactionStore;
    private final BalanceEngine balanceEngine;

    public BalanceTrackerService(TransactionStore transactionStore,
                                 BalanceEngine balanceEngine) {
        this.transactionStore = transactionStore;
        this.balanceEngine = balanceEngine;
    }

    @Cacheable(value = "transactions", key = "#userId")
    @Transactional
    public List<TransactionEntity> getUserTransactions(UUID userId) {
        return transactionStore.findByUserId(userId);
    }

    @CacheEvict(value = "transactions", key = "#userId")
    @Transactional
    public void deleteUserTransaction(UUID transactionId, UUID userId) {
        transactionStore.deleteByIdAndUserId(userId, transactionId);
    }

    @CachePut(value = "transactions", key = "#userId")
    @Transactional
    public TransactionEntity saveUserTransaction(UUID userId, UUID transactionId, Transaction transaction) {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .id(transactionId)
                .userId(userId)
                .sourceName(transaction.sourceName())
                .amount(BigDecimal.valueOf(transaction.amount()))
                .date(transaction.date())
                .type(transaction.type())
                .payPeriod(transaction.payPeriod())
                .build();
        transactionStore.save(transactionEntity);
        logger.info("Saved transaction {}", transactionEntity);
        return transactionEntity;
    }

    public List<DataPointPerDate> getBalanceSummary(BalanceDataRequest request) {
        return balanceEngine.getBalanceSummary(request);
    }
}

