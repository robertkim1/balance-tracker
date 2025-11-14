package com.pikel.balancetracker.entity;

import com.pikel.balancetracker.balance.model.PayPeriod;
import com.pikel.balancetracker.balance.model.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a financial transaction (debt, income, or recurring payment).
 * Each transaction belongs to a specific user and tracks cash flow events.
 */
@Entity
@Table(name = "Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who owns this transaction.
     * Uses lazy loading for performance - user details loaded only when accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Human-readable source/description of the transaction.
     * Example: "apple thru chase", "Salary - Acme Corp"
     */
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    /**
     * Transaction amount in dollars.
     * Uses BigDecimal for precise monetary calculations.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Date when this transaction occurs or is due.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Type of transaction: DEBT, INCOME
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_period", length = 50)
    private PayPeriod payPeriod;
}