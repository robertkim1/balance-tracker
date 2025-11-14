package com.pikel.balancetracker.security;

import com.pikel.balancetracker.entity.User;
import com.pikel.balancetracker.entity.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * This filter runs on EVERY request to protected endpoints (Step 9 of our flow).
 * It extracts the JWT from the Authorization header, validates it, and loads the User.
 *
 * Flow for API calls:
 * 1. Frontend sends: Authorization: Bearer <jwt_token>
 * 2. This filter extracts the token
 * 3. Validates the token signature and expiration
 * 4. Extracts user ID from token
 * 5. Loads User from database
 * 6. Sets the User in Spring Security context
 * 7. Controller can now access the authenticated user
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract JWT from Authorization header
            // Expected format: "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Token is valid, extract user ID
                Long userId = tokenProvider.getUserIdFromToken(jwt);

                // Load full User object from database
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId));

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,                    // Principal - this is what @AuthenticationPrincipal gives you
                                null,                    // Credentials - not needed after authentication
                                Collections.emptyList()  // Authorities/roles - add later if needed
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Tell Spring Security this user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("User authenticated: {}", user.getEmail());
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue with the request
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from the Authorization header.
     * Expected format: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }
}