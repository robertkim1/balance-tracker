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

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerService.class);

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
    public TransactionEntity saveUserTransaction(UUID userId, Transaction transaction) {
        try {
            TransactionEntity transactionEntity = TransactionEntity.builder()
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BalanceTrackerException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    public List<DataPointPerDate> getBalanceSummary(BalanceDataRequest request) {
        return balanceEngine.getBalanceSummary(request);
    }
}

