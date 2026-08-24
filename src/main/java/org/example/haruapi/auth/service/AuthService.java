package org.example.haruapi.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.auth.dto.request.LoginRequest;
import org.example.haruapi.auth.dto.response.LoginResponse;
import org.example.haruapi.auth.exception.InvalidCredentialsException;
import org.example.haruapi.global.security.auth.HaruUserDetails;
import org.example.haruapi.global.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            normalizeEmail(request.email()),
                            request.password()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException();
        }

        HaruUserDetails principal =
                (HaruUserDetails) authentication.getPrincipal();
        JwtTokenProvider.AccessToken accessToken =
                jwtTokenProvider.issueAccessToken(
                        principal.getUserId(),
                        List.of(principal.getRole().name())
                );

        return LoginResponse.bearer(
                accessToken.value(),
                accessToken.expiresAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
