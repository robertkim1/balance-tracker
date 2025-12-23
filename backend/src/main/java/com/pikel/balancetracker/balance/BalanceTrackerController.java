package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalanceDataRequest;
import com.pikel.balancetracker.balance.model.DataPointPerDate;
import com.pikel.balancetracker.balance.entity.TransactionEntity;
import com.pikel.balancetracker.balance.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/balance")
public class BalanceTrackerController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerController.class);
    private final BalanceTrackerService balanceTrackerService;

    public BalanceTrackerController(BalanceTrackerService balanceTrackerService) {
        this.balanceTrackerService = balanceTrackerService;
    }

    /**
     * GET endpoint - Fetch user's existing transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> getUserTransactions(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        logger.info("Fetching transactions for userid: {}", userId);

        List<TransactionEntity> transactions = balanceTrackerService.getUserTransactions(userId);

        logger.info("Found {} transactions for user: {}", transactions.size(), userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionEntity> createUserTransaction(@RequestBody Transaction transaction,
                                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID transactionId = UUID.randomUUID();
        TransactionEntity saved = balanceTrackerService.saveUserTransaction(userId, transactionId, transaction);
        logger.info("Created transaction for userid: {}", userId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()      // /api/transactions
                .path("/{id}")             // /api/transactions/{id}
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // update endpoint
    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionEntity> updateUserTransaction(@PathVariable UUID id, @RequestBody Transaction transaction,
                                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        TransactionEntity saved = balanceTrackerService.saveUserTransaction(userId, id, transaction);
        logger.info("Updated transaction for userid: {}, transactionid: {}", userId, id);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()      // /api/transactions
                .path("/{id}")             // /api/transactions/{id}
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteUserTransaction(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        balanceTrackerService.deleteUserTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST endpoint - Submit balance data, save transactions, and get balance summary
     */
    @PostMapping("/submit")
    public ResponseEntity<List<DataPointPerDate>> submitBalanceData(
            @RequestBody BalanceDataRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        String userEmail = jwt.getClaimAsString("email");

        logger.info("Received balance data request from user: {} (ID: {}) with {} transactions",
                userEmail,
                userId,
                request.transactions() != null ? request.transactions().size() : 0);

        // Save transactions (this will replace existing ones)
//        balanceTrackerService.saveUserTransactions(userId, request.transactions());

        // Calculate and return balance summary
        List<DataPointPerDate> balanceSummary = balanceTrackerService.getBalanceSummary(request);

        logger.info("Successfully saved transactions and generated balance summary for user: {}", userEmail);
        return ResponseEntity.ok(balanceSummary);
    }
}