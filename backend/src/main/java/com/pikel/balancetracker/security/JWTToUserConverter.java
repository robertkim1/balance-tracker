package com.pikel.balancetracker.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;

@Component
public class JWTToUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        UserPrincipal principal = new UserPrincipal(userId);

        return new UsernamePasswordAuthenticationToken(
                principal,
                jwt,
                List.of()
        );
    }
}
