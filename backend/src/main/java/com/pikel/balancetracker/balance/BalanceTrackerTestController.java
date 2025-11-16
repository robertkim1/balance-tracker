package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalanceDataRequest;
import com.pikel.balancetracker.balance.model.DataPointPerDate;
import com.pikel.balancetracker.entity.User;
import com.pikel.balancetracker.entity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Test controller for development - bypasses authentication.
 * Only active in 'dev' profile for security.
 */
@RestController
@RequestMapping("/api/test/balance")
@Profile("dev") // Only active when spring.profiles.active=dev
public class BalanceTrackerTestController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerTestController.class);

    private final BalanceTrackerService balanceTrackerService;
    private final UserRepository userRepository;

    public BalanceTrackerTestController(BalanceTrackerService balanceTrackerService,
                          UserRepository userRepository) {
        this.balanceTrackerService = balanceTrackerService;
        this.userRepository = userRepository;
    }

    /**
     * Test endpoint that works without authentication.
     * Creates or uses a test user automatically.
     *
     * POST http://localhost:8080/api/test/balance/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<List<DataPointPerDate>> testBalanceSubmit(
            @RequestBody BalanceDataRequest request) {

        // Get or create a test user
        User testUser = getOrCreateTestUser();

        logger.info("TEST: Processing balance request for test user: {}", testUser.getEmail());

        List<DataPointPerDate> balanceSummary = balanceTrackerService.getBalanceSummary(request);

        logger.info("TEST: Successfully generated balance summary with {} data points",
                balanceSummary.size());

        return ResponseEntity.ok(balanceSummary);
    }

    /**
     * Get test user info - useful to see what test user ID exists.
     *
     * GET http://localhost:8080/api/test/user
     */
    @GetMapping("/user")
    public ResponseEntity<User> getTestUser() {
        User testUser = getOrCreateTestUser();
        return ResponseEntity.ok(testUser);
    }

    /**
     * Get or create a test user for development.
     * This simulates what would happen after Google OAuth.
     */
    private User getOrCreateTestUser() {
        return userRepository.findByEmail("test@example.com")
                .orElseGet(() -> {
                    logger.info("TEST: Creating new test user");
                    User newUser = new User();
                    newUser.setGoogleId("test-google-id-12345");
                    newUser.setEmail("test@example.com");
                    newUser.setName("Test User");
                    newUser.setPicture("https://example.com/avatar.jpg");
                    return userRepository.save(newUser);
                });
    }
}