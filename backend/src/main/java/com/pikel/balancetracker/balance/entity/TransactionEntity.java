package com.pikel.balancetracker.balance.entity;

import com.pikel.balancetracker.balance.model.PayPeriod;
import com.pikel.balancetracker.balance.model.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Foreign key reference to auth.users(id) in Supabase.
     * This links the transaction to the authenticated user.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Name/description of the transaction source (e.g., "apple thru chase", "Salary")
     */
    @Column(name = "source_name", nullable = false)
    private String sourceName;

    /**
     * Transaction amount (positive for both income and debts)
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Date when the transaction occurs or is due
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Type of transaction: INCOME or DEBT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    /**
     * How frequently this transaction recurs
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_period", nullable = false, length = 20)
    private PayPeriod payPeriod;

    /**
     * Timestamp when this record was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when this record was last updated
     */
    @Column(name = "updated_at")
    private Instant updatedAt;

    // JPA lifecycle hooks
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}