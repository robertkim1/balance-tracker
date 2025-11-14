package com.pikel.balancetracker.security;

import com.pikel.balancetracker.user.User;
import com.pikel.balancetracker.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This service is called automatically by Spring Security after Google OAuth succeeds.
 * It receives the decoded user info from Google's JWT and creates/updates our User record.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // This line does the heavy lifting:
        // - Extracts id_token from Google's response
        // - Decodes the Base64URL JWT parts
        // - Validates signature using Google's public keys
        // - Parses the JSON payload into attributes
        OAuth2User oauth2User = super.loadUser(userRequest);

        logger.info("Processing OAuth2 login for user: {}", Optional.ofNullable(oauth2User.getAttribute("email")));

        // Extract user info from Google's JWT payload
        // These come from the "payload" section of Google's id_token
        String googleId = oauth2User.getAttribute("sub");        // Google's unique user ID
        String email = oauth2User.getAttribute("email");         // User's email
        String name = oauth2User.getAttribute("name");           // Full name
        String picture = oauth2User.getAttribute("picture");     // Profile picture URL

        // Find existing user or create new one in OUR database
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    logger.info("Creating new user with Google ID: {}", googleId);
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPicture(picture);
                    return userRepository.save(newUser);
                });

        // Update user info in case it changed in Google
        if (!user.getEmail().equals(email) || !user.getName().equals(name)) {
            logger.info("Updating user info for: {}", email);
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
            userRepository.save(user);
        }

        // Return the OAuth2User - Spring Security will use this
        return oauth2User;
    }
}