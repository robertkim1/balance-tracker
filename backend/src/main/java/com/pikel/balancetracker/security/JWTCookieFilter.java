package com.pikel.balancetracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JWTCookieFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    public JWTCookieFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    try {
                        Jwt jwt = jwtDecoder.decode(cookie.getValue());
                        // Populate SecurityContext so @AuthenticationPrincipal works
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(jwt, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } catch (JwtException e) {
                        // Invalid token, ignore or log
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
