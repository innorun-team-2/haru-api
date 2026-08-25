package org.example.haruapi.auth.dto.response;

import java.time.Instant;

public record LoginResponse(
        String tokenType,
        String accessToken,
        Instant expiresAt
) {
    public static LoginResponse bearer(
            String accessToken,
            Instant expiresAt
    ) {
        return new LoginResponse("Bearer", accessToken, expiresAt);
    }
}
