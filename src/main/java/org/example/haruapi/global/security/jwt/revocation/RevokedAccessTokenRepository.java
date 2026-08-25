package org.example.haruapi.global.security.jwt.revocation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedAccessTokenRepository
        extends JpaRepository<RevokedAccessToken, Long> {

    boolean existsByTokenId(String tokenId);

    long deleteByExpiresAtLessThanEqual(Instant expiresAt);
}
