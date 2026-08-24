package org.example.haruapi.global.security.jwt;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;
    private final Clock clock;

    public JwtTokenProvider(
            JwtEncoder jwtEncoder,
            JwtProperties properties,
            Clock jwtClock
    ) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
        this.clock = jwtClock;
    }

    public AccessToken issueAccessToken(
            Long userId,
            Collection<String> roles
    ) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());
        List<String> normalizedRoles = roles == null
                ? List.of()
                : roles.stream().distinct().sorted().toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .audience(List.of(properties.audience()))
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString())
                .claim("roles", normalizedRoles)
                .build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        String value = jwtEncoder.encode(
                JwtEncoderParameters.from(headers, claims)
        ).getTokenValue();

        return new AccessToken(value, expiresAt);
    }

    public record AccessToken(String value, Instant expiresAt) {
    }
}
