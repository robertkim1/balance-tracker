package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.entity.TransactionEntity;
import com.pikel.balancetracker.exception.BalanceTrackerException;
import com.pikel.balancetracker.balance.model.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceEngine.class);

    private final TransactionStore transactionStore;
    private final BalanceEngine balanceEngine;

    public BalanceTrackerService(TransactionStore transactionStore,
                                 BalanceEngine balanceEngine) {
        this.transactionStore = transactionStore;
        this.balanceEngine = balanceEngine;
    }

    public List<TransactionEntity> getUserTransactions(UUID userId) {
        return transactionStore.findByUserId(userId);
    }

    @Transactional
    public void saveUserTransactions(UUID userId, List<Transaction> transactions) {
        try {
            // Delete all existing transactions for this user
            transactionStore.deleteByUserId(userId);
            logger.info("Deleted existing transactions for user: {}", userId);

            // Convert and save new transactions
            List<TransactionEntity> entities = transactions.stream()
                    .map(t -> TransactionEntity.builder()
                            .userId(userId)
                            .sourceName(t.sourceName())
                            .amount(BigDecimal.valueOf(t.amount()))
                            .date(t.date())
                            .type(t.type())
                            .payPeriod(t.payPeriod())
                            .build())
                    .toList();

            transactionStore.saveAll(entities);
            logger.info("Saved {} new transactions for user: {}", entities.size(), userId);

        } catch (Exception e) {
            logger.error("Failed to save transactions for user: {}", userId, e);
            throw new BalanceTrackerException("Failed to save transactions: " + e.getMessage(), e);
        }
    }

    public List<DataPointPerDate> getBalanceSummary(BalanceDataRequest request) {
        return balanceEngine.getBalanceSummary(request);
    }
}

