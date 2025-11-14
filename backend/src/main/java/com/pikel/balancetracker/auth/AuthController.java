package com.pikel.balancetracker.auth;

import com.pikel.balancetracker.entity.User;
import com.pikel.balancetracker.entity.UserRepository;
import com.pikel.balancetracker.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Handles manual Google OAuth2 authentication flow.
 * Frontend sends authorization code, backend exchanges it for tokens,
 * verifies user, and returns JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public AuthController(JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          WebClient.Builder webClientBuilder) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.webClient = webClientBuilder.build();
    }

    /**
     * Exchange Google authorization code for user authentication.
     *
     * Frontend flow:
     * 1. User clicks "Sign in with Google"
     * 2. Frontend redirects to Google OAuth
     * 3. Google redirects back with authorization code
     * 4. Frontend sends code to this endpoint
     * 5. Backend exchanges code for tokens, creates/updates user, returns JWT
     *
     * @param request containing the authorization code from Google
     * @return JWT token and user info
     */
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            logger.info("Received Google auth request");

            // Step 1: Exchange authorization code for Google tokens
            GoogleTokenResponse tokenResponse = exchangeCodeForTokens(request.code());

            // Step 2: Get user info from Google
            GoogleUserInfo userInfo = getUserInfoFromGoogle(tokenResponse.accessToken());

            logger.info("Successfully retrieved user info for: {}", userInfo.email());

            // Step 3: Create or update user in our database
            User user = createOrUpdateUser(userInfo);

            // Step 4: Generate OUR JWT token
            String jwt = tokenProvider.generateToken(user);

            logger.info("Generated JWT for user: {}", user.getEmail());

            // Step 5: Return JWT and user info to frontend
            return ResponseEntity.ok(new AuthResponse(jwt, user));

        } catch (Exception e) {
            logger.error("Error during Google authentication", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    /**
     * Exchange authorization code for access token and id_token from Google.
     */
    private GoogleTokenResponse exchangeCodeForTokens(String code) {
        logger.info("Exchanging authorization code for tokens");

        // Make POST request to Google's token endpoint
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .bodyValue(Map.of(
                        "code", code,
                        "client_id", googleClientId,
                        "client_secret", googleClientSecret,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"
                ))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    /**
     * Get user information from Google using the access token.
     */
    private GoogleUserInfo getUserInfoFromGoogle(String accessToken) {
        logger.info("Fetching user info from Google");

        return webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }

    /**
     * Create new user or update existing user in our database.
     */
    private User createOrUpdateUser(GoogleUserInfo userInfo) {
        return userRepository.findByGoogleId(userInfo.id())
                .map(existingUser -> {
                    // Update existing user info
                    logger.info("Updating existing user: {}", userInfo.email());
                    existingUser.setEmail(userInfo.email());
                    existingUser.setName(userInfo.name());
                    existingUser.setPicture(userInfo.picture());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Create new user
                    logger.info("Creating new user: {}", userInfo.email());
                    User newUser = new User();
                    newUser.setGoogleId(userInfo.id());
                    newUser.setEmail(userInfo.email());
                    newUser.setName(userInfo.name());
                    newUser.setPicture(userInfo.picture());
                    return userRepository.save(newUser);
                });
    }

    // Request/Response DTOs
    public record GoogleAuthRequest(String code) {}

    public record GoogleTokenResponse(
            String accessToken,
            String expiresIn,
            String tokenType,
            String scope,
            String idToken
    ) {}

    public record GoogleUserInfo(
            String id,
            String email,
            String verifiedEmail,
            String name,
            String givenName,
            String familyName,
            String picture
    ) {}

    public record AuthResponse(
            String token,
            UserInfo user
    ) {
        public AuthResponse(String token, User user) {
            this(token, new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPicture()
            ));
        }
    }

    public record UserInfo(
            Long id,
            String email,
            String name,
            String picture
    ) {}
}