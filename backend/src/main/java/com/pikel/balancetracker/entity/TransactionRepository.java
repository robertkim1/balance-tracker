package com.pikel.balancetracker.entity;

import com.pikel.balancetracker.balance.model.TransactionType;
import com.pikel.balancetracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Transaction entity.
 * Provides data access methods for financial transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific user.
     *
     * @param userId the user's ID
     * @return list of transactions ordered by date descending
     */
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Find all transactions for a user within a date range.
     *
     * @param userId the user's ID
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of transactions ordered by date ascending
     */
    List<Transaction> findByUserIdAndDateBetweenOrderByDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find all transactions for a user before or on a specific date.
     * Used for balance projection calculations.
     *
     * @param userId the user's ID
     * @param endDate the cutoff date (inclusive)
     * @return list of transactions ordered by date ascending
     */
    List<Transaction> findByUserIdAndDateLessThanEqualOrderByDateAsc(
            Long userId, LocalDate endDate);

    /**
     * Find transactions by type for a specific user.
     *
     * @param userId the user's ID
     * @param type the transaction type (DEBT or INCOME)
     * @return list of transactions of that type
     */
    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    /**
     * Delete all transactions for a specific user.
     * Useful for testing or user account deletion.
     *
     * @param userId the user's ID
     * @return number of transactions deleted
     */
    Long deleteByUserId(Long userId);

    /**
     * Count total transactions for a user.
     *
     * @param userId the user's ID
     * @return count of transactions
     */
    long countByUserId(Long userId);

    /**
     * Check if a user has any transactions.
     *
     * @param userId the user's ID
     * @return true if user has at least one transaction
     */
    boolean existsByUserId(Long userId);
}