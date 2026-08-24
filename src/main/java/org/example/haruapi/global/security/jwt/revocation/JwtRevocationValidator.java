package org.example.haruapi.global.security.jwt.revocation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtRevocationValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_TOKEN = new OAuth2Error(
            "invalid_token",
            "The token is revoked or has no token identifier",
            null
    );

    private final TokenRevocationService tokenRevocationService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String tokenId = jwt.getClaimAsString(JwtClaimNames.JTI);

        if (tokenId == null || tokenRevocationService.isRevoked(tokenId)) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
