package org.example.haruapi.global.security.jwt.revocation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "revoked_access_tokens",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_revoked_access_tokens_token_id",
                columnNames = "token_id"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RevokedAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, updatable = false, length = 36)
    private String tokenId;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;

    private RevokedAccessToken(String tokenId, Instant expiresAt) {
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
    }

    public static RevokedAccessToken create(
            String tokenId,
            Instant expiresAt
    ) {
        return new RevokedAccessToken(tokenId, expiresAt);
    }
}
