package org.example.haruapi.global.security.jwt.revocation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final Clock jwtClock;

    @Transactional
    public void revoke(Jwt jwt) {
        String tokenId = jwt.getClaimAsString(JwtClaimNames.JTI);
        Instant expiresAt = jwt.getExpiresAt();

        if (tokenId == null || expiresAt == null) {
            throw new IllegalArgumentException(
                    "JWT must contain jti and exp claims"
            );
        }

        revokedAccessTokenRepository.deleteByExpiresAtLessThanEqual(
                jwtClock.instant()
        );

        if (!revokedAccessTokenRepository.existsByTokenId(tokenId)) {
            revokedAccessTokenRepository.save(
                    RevokedAccessToken.create(tokenId, expiresAt)
            );
        }
    }

    @Transactional(readOnly = true)
    public boolean isRevoked(String tokenId) {
        return revokedAccessTokenRepository.existsByTokenId(tokenId);
    }
}
