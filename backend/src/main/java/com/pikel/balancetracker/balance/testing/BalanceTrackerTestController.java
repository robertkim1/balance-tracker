package com.pikel.balancetracker.balance.testing;

import com.pikel.balancetracker.security.JwtTokenProvider;
import com.pikel.balancetracker.user.User;
import com.pikel.balancetracker.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TEST ONLY - Endpoint to generate JWT tokens for testing without OAuth2.
 * Only enabled in dev profile for security.
 */
@RestController
@RequestMapping("/api/test/auth")
@Profile("dev")  // Only active when spring.profiles.active=dev
public class BalanceTrackerTestController {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public BalanceTrackerTestController(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * Generate a JWT token for a test user.
     * POST http://localhost:8080/api/test/auth/token
     * Body: { "email": "test@example.com" }
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generateTestToken(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Find or create test user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId("test-google-id-" + System.currentTimeMillis());
                    newUser.setEmail(email);
                    newUser.setName("Test User");
                    newUser.setPicture("https://via.placeholder.com/150");
                    return userRepository.save(newUser);
                });

        // Generate JWT
        String token = tokenProvider.generateToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId().toString());
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }
}