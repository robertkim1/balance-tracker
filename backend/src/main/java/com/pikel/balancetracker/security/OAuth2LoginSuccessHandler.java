package com.pikel.balancetracker.security;

import com.pikel.balancetracker.entity.User;
import com.pikel.balancetracker.entity.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * redirects user after
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(
            JwtTokenProvider tokenProvider,
            UserRepository userRepository,
            @Value("${app.frontend.url}") String frontendUrl) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oauth2User.getAttribute("sub");

        logger.info("OAuth2 authentication successful for Google ID: {}", googleId);

        User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

        String jwtToken = tokenProvider.generateToken(user);

        logger.info("Generated JWT token for user: {}", user.getEmail());

        // Build redirect URL dynamically based on environment
        // Dev: http://localhost:3000/auth/callback?token=...
        // Prod: https://myapp.com/auth/callback?token=...
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("token", jwtToken)
                .build()
                .toUriString();

        logger.info("Redirecting to: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}