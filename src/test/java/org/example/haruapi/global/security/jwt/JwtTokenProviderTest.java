package org.example.haruapi.global.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET =
            "VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=";
    private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    void accessTokenContainsRequiredClaimsAndCanBeDecoded() {
        JwtProperties properties = properties("haru-client");
        JwtConfig config = new JwtConfig();
        var key = config.jwtSecretKey(properties);
        var encoder = config.jwtEncoder(key);
        JwtDecoder decoder = config.jwtDecoder(key, properties);
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(encoder, properties, clock);

        JwtTokenProvider.AccessToken accessToken =
                provider.issueAccessToken(42L, List.of("USER", "USER"));
        Jwt decoded = decoder.decode(accessToken.value());

        assertThat(decoded.getClaimAsString("iss")).isEqualTo("haru-api");
        assertThat(decoded.getAudience()).containsExactly("haru-client");
        assertThat(decoded.getSubject()).isEqualTo("42");
        assertThat(decoded.getClaimAsStringList("roles")).containsExactly("USER");
        assertThat(decoded.getId()).isNotBlank();
        assertThat(accessToken.expiresAt()).isEqualTo(NOW.plusSeconds(900));
    }

    @Test
    void decoderRejectsWrongAudience() {
        JwtProperties tokenProperties = properties("another-client");
        JwtProperties decoderProperties = properties("haru-client");
        JwtConfig config = new JwtConfig();
        var key = config.jwtSecretKey(tokenProperties);
        var encoder = config.jwtEncoder(key);
        JwtTokenProvider provider = new JwtTokenProvider(
                encoder,
                tokenProperties,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        String token = provider.issueAccessToken(1L, List.of("USER")).value();
        JwtDecoder decoder = config.jwtDecoder(key, decoderProperties);

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    void secretMustDecodeToAtLeast256Bits() {
        JwtProperties properties = new JwtProperties(
                "haru-api",
                "haru-client",
                Duration.ofMinutes(15),
                "dG9vLXNob3J0"
        );

        assertThatThrownBy(() -> new JwtConfig().jwtSecretKey(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    private JwtProperties properties(String audience) {
        return new JwtProperties(
                "haru-api",
                audience,
                Duration.ofMinutes(15),
                SECRET
        );
    }
}
