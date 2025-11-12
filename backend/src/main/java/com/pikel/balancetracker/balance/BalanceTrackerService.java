package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.exception.BalanceTrackerException;
import com.pikel.balancetracker.balance.model.*;
import com.pikel.balancetracker.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BalanceTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerService.class);
    private static final int MAX_PROJECTION_YEARS = 5;
    private static final int MAX_TRANSACTIONS = 1000;

    /**
     * return a list of balances based on our selected timeframe and summarize date by values
     *
     * @param request from with lists of debts, incomes, current balance, etc
     * @return full balance summary
     */
    public List<DatedBalance> getBalanceSummary(BalanceDataRequest request) {
        validateRequest(request);

        try {
            // collect request parameters
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusYears(request.projectionTimeframe().getYears());
            List<Transaction> transactions = getTransactions(request, startDate);

            PriorityQueue<Transaction> transactionQueue = new PriorityQueue<>(Comparator.comparing(Transaction::date));
            transactionQueue.addAll(transactions);

            SummarizeDateBy summarizeDateBy = request.summarizeDateBy();
            double currBalance = request.currentBalance();

            // output balance list pairing balance number to a date
            List<DatedBalance> outputBalanceList = new ArrayList<>();

            LocalDate currDate = startDate;

            while (!currDate.isAfter(endDate)) {
                currBalance = processQueue(transactionQueue, currDate, currBalance);
                outputBalanceList.add(new DatedBalance(currBalance, currDate));
                currDate = incrementDate(currDate, summarizeDateBy);
            }

            logger.info("Generated {} balance data points", outputBalanceList.size());
            return outputBalanceList;

        } catch (BalanceTrackerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in balance calculation", e);
            throw new BalanceTrackerException("Failed to calculate balance summary: " + e.getMessage(), e);
        }
    }

    private static List<Transaction> getTransactions(BalanceDataRequest request, LocalDate startDate) {
        List<Transaction> transactions = request.transactions();

        // Validate transaction dates
        for (Transaction transaction : transactions) {
            if (transaction.date() == null) {
                throw new BalanceTrackerException("Transaction date cannot be null for: " + transaction.sourceName());
            }
            if (transaction.date().isBefore(startDate)) {
                throw new BalanceTrackerException("Transaction date cannot be before start date: " + transaction.sourceName());
            }
        }
        return transactions;
    }

    private void validateRequest(BalanceDataRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.transactions() == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }

        if (request.transactions().size() > MAX_TRANSACTIONS) {
            throw new BalanceTrackerException("Too many transactions. Maximum allowed: " + MAX_TRANSACTIONS);
        }

        if (request.projectionTimeframe() == null) {
            throw new IllegalArgumentException("Projection timeframe cannot be null");
        }

        if (request.projectionTimeframe().getYears() > MAX_PROJECTION_YEARS) {
            throw new BalanceTrackerException("Projection timeframe too long. Maximum: " + MAX_PROJECTION_YEARS + " years");
        }

        if (request.summarizeDateBy() == null) {
            throw new IllegalArgumentException("SummarizeDateBy cannot be null");
        }

        // Validate each transaction
        for (Transaction transaction : request.transactions()) {
            validateTransaction(transaction);
        }
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (transaction.sourceName() == null || transaction.sourceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction source name cannot be empty");
        }

        if (transaction.amount() < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative for: " + transaction.sourceName());
        }

        if (transaction.amount() == 0) {
            logger.warn("Transaction with zero amount detected: {}", transaction.sourceName());
        }

        if (transaction.date() == null) {
            throw new IllegalArgumentException("Transaction date cannot be null for: " + transaction.sourceName());
        }

        if (transaction.type() == null) {
            throw new IllegalArgumentException("Transaction type cannot be null for: " + transaction.sourceName());
        }

        if (transaction.payPeriod() == null) {
            throw new IllegalArgumentException("Transaction pay period cannot be null for: " + transaction.sourceName());
        }
    }

    private double processQueue(
            PriorityQueue<Transaction> queue,
            LocalDate currDate,
            double currBalance
    ) {
        if (queue.isEmpty()) return currBalance;

        int processedCount = 0;
        int maxProcessPerDate = 100; // Prevent infinite loops

        while (!queue.isEmpty() && (!queue.peek().date().isAfter(currDate))) {
            if (++processedCount > maxProcessPerDate) {
                throw new BalanceTrackerException("Too many transactions on the same date: " + currDate);
            }

            Transaction currTransaction = queue.poll();
            if (currTransaction != null) {
                double amount = currTransaction.amount();
                currBalance += currTransaction.type() == TransactionType.INCOME ? amount : -amount;
                // since date is always updated here we just step once
                Transaction newTransaction = nextTransactionOccurrence(currTransaction, currTransaction.date());
                queue.add(newTransaction);
            }
        }
        return currBalance;
    }

    private Transaction nextTransactionOccurrence(Transaction currTransaction, LocalDate currDate) {
        try {
            var nextDate = switch (currTransaction.payPeriod()) {
                case WEEKLY -> currDate.plusWeeks(1);
                case SEMIMONTHLY -> DateUtils.findNext1stOr15th(currDate);
                case MONTHLY -> currDate.plusMonths(1);
            };
            return currTransaction.withDate(nextDate);
        } catch (Exception e) {
            throw new BalanceTrackerException("Failed to calculate next transaction occurrence for: " +
                    currTransaction.sourceName(), e);
        }
    }

    private LocalDate incrementDate(LocalDate date, SummarizeDateBy summarizeDateBy) {
        return switch (summarizeDateBy) {
            case DAY -> date.plusDays(1);
            case WEEK -> date.plusWeeks(1);
            case MONTH -> date.plusMonths(1);
        };
    }
}

