package com.pikel.balancetracker.entity;

import com.pikel.balancetracker.balance.model.TransactionType;
import com.pikel.balancetracker.balance.model.PayPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Transaction entity operations.
 * Provides methods to query user-specific transactions with various filters.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Find all transactions for a specific user.
     *
     * @param userId the user's UUID from auth.users
     * @return list of transactions ordered by date ascending
     */
    List<Transaction> findByUserIdOrderByDateAsc(UUID userId);

    /**
     * Find all transactions for a user by transaction type.
     *
     * @param userId the user's UUID
     * @param type the transaction type (INCOME or DEBT)
     * @return list of filtered transactions
     */
    List<Transaction> findByUserIdAndType(UUID userId, TransactionType type);

    /**
     * Find all transactions for a user within a date range.
     *
     * @param userId the user's UUID
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of transactions in date range
     */
    List<Transaction> findByUserIdAndDateBetweenOrderByDateAsc(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Find all transactions for a user by pay period.
     *
     * @param userId the user's UUID
     * @param payPeriod the pay period filter
     * @return list of transactions with matching pay period
     */
    List<Transaction> findByUserIdAndPayPeriod(UUID userId, PayPeriod payPeriod);

    /**
     * Find all transactions for a user after a specific date.
     *
     * @param userId the user's UUID
     * @param date the cutoff date
     * @return list of transactions after the date
     */
    List<Transaction> findByUserIdAndDateAfterOrderByDateAsc(UUID userId, LocalDate date);

    /**
     * Find all transactions for a user before a specific date.
     *
     * @param userId the user's UUID
     * @param date the cutoff date
     * @return list of transactions before the date
     */
    List<Transaction> findByUserIdAndDateBeforeOrderByDateAsc(UUID userId, LocalDate date);

    /**
     * Count total transactions for a user.
     *
     * @param userId the user's UUID
     * @return count of transactions
     */
    long countByUserId(UUID userId);

    /**
     * Delete all transactions for a user.
     *
     * @param userId the user's UUID
     * @return number of deleted transactions
     */
    long deleteByUserId(UUID userId);

    /**
     * Check if a user has any transactions.
     *
     * @param userId the user's UUID
     * @return true if user has at least one transaction
     */
    boolean existsByUserId(UUID userId);
}