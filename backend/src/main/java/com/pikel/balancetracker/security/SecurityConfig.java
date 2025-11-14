package com.pikel.balancetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Main Security Configuration.
 *
 * This sets up TWO authentication mechanisms:
 * 1. OAuth2 Login - For initial "Sign in with Google" (Steps 1-7)
 * 2. JWT Authentication - For subsequent API calls (Steps 8-10)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF - we're using JWT tokens (stateless)
                .csrf(csrf -> csrf.disable())

                // Enable CORS for frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure OAuth2 Login (Google Sign In)
                .oauth2Login(oauth2 -> oauth2
                        // Use our custom service to handle Google user info
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // Use our custom handler to generate JWT after login
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // Configure URL-based authorization
                .authorizeHttpRequests(auth -> auth
                        // Allow OAuth2 endpoints (Google redirects here)
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()

                        // Require authentication for all /api/** endpoints
                        .requestMatchers("/api/**").authenticated()

                        // Allow everything else (if any)
                        .anyRequest().permitAll()
                )

                // Stateless session - we don't use HTTP sessions, only JWTs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add our JWT filter before Spring Security's default authentication filter
                // This runs on every request to validate the JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure CORS to allow requests from your frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}