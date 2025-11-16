package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalanceDataRequest;
import com.pikel.balancetracker.balance.model.DataPointPerDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/submit")
    public ResponseEntity<List<DataPointPerDate>> submitBalanceData(
            @RequestBody BalanceDataRequest request,
            @AuthenticationPrincipal Jwt jwt) {  // ← Spring injects validated JWT here

        // Extract user information from the JWT
        UUID userId = UUID.fromString(jwt.getSubject());  // User's UUID from auth.users
        String userEmail = jwt.getClaimAsString("email");  // User's email

        logger.info("Received balance data request from user: {} (ID: {}) with {} transactions",
                userEmail,
                userId,
                request.transactions() != null ? request.transactions().size() : 0);

        List<DataPointPerDate> balanceSummary = balanceTrackerService.getBalanceSummary(request);

        logger.info("Successfully generated balance summary for user: {}", userEmail);
        return ResponseEntity.ok(balanceSummary);
    }
}