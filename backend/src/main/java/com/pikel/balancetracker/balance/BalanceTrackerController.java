package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalanceDataRequest;
import com.pikel.balancetracker.balance.model.DataPointPerDate;
import com.pikel.balancetracker.balance.entity.TransactionEntity;
import com.pikel.balancetracker.balance.model.User;
import com.pikel.balancetracker.utils.StringToUUIDMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/userdata")
    public ResponseEntity<User> getUserData(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = StringToUUIDMapper.fromBetterAuthId(jwt.getSubject());
        System.out.println(userId);
        String userEmail = jwt.getClaimAsString("email");
        return ResponseEntity.ok(new User(userId, userEmail));
    }

    /**
     * GET endpoint - Fetch user's existing transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> getUserTransactions(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        String userEmail = jwt.getClaimAsString("email");

        logger.info("Fetching transactions for user: {} (ID: {})", userEmail, userId);

        List<TransactionEntity> transactions = balanceTrackerService.getUserTransactions(userId);

        logger.info("Found {} transactions for user: {}", transactions.size(), userEmail);
        return ResponseEntity.ok(transactions);
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
        balanceTrackerService.saveUserTransactions(userId, request.transactions());

        // Calculate and return balance summary
        List<DataPointPerDate> balanceSummary = balanceTrackerService.getBalanceSummary(request);

        logger.info("Successfully saved transactions and generated balance summary for user: {}", userEmail);
        return ResponseEntity.ok(balanceSummary);
    }
}