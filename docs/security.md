# Security and JWT architecture

## Request authentication flow

1. The client sends an access token in `Authorization: Bearer <token>`.
2. Spring Security's OAuth2 Resource Server extracts the bearer token.
3. `JwtDecoder` verifies the HS256 signature and validates `iss`, `aud`, `exp`, and `nbf`.
4. The `roles` claim is converted to Spring authorities with the `ROLE_` prefix.
5. The authenticated principal is stored in the `SecurityContext` for the request.

JWT parsing is intentionally delegated to Spring Security. Do not add a custom
`OncePerRequestFilter` for the same purpose.

## Endpoint policy

The following endpoints are public:

- `POST /api/users`
- `POST /api/auth/login`
- `GET /actuator/health`

`/api/admin/**` requires `ROLE_ADMIN`. Every other endpoint requires a valid
access token. This includes `PUT /api/users/me`, `DELETE /api/users/me`, and
`POST /api/auth/logout`. Method-level authorization can be added with
`@PreAuthorize`.

## Access token

`JwtTokenProvider` issues HS256 access tokens with these claims:

- `iss`: configured issuer
- `aud`: configured audience
- `sub`: immutable user ID
- `iat`, `exp`: issue and expiry time
- `jti`: unique token ID
- `roles`: role names without the `ROLE_` prefix, for example `USER`

The default lifetime is 15 minutes. Do not add passwords, emails, or other
sensitive personal data to the token.

## Secret configuration

Generate a secret with at least 256 bits:

```shell
openssl rand -base64 32
```

Set the generated value as `JWT_SECRET_BASE64`. The application intentionally
fails at startup when the value is absent, invalid Base64, or shorter than 32
decoded bytes. Never commit the actual value.

HS256 is suitable while token issuance and verification live in this single
service. Move to an asymmetric algorithm such as RS256 or ES256 before sharing
token verification with independently operated services.

## Error contract

- Missing, malformed, invalid, or expired authentication: HTTP 401 with
  `AUTHENTICATION_REQUIRED`
- Authenticated user without sufficient authority: HTTP 403 with `ACCESS_DENIED`

Both responses use the project's common `ApiResponse` JSON shape.

## Authentication features still to implement

This security foundation does not implement the signup, user update, user
deletion, login, or logout business APIs. Their next implementation should add:

1. `UserRepository`, unique normalized email, and a user role.
2. Signup using the configured `PasswordEncoder` before persistence.
3. Login credential verification followed by `JwtTokenProvider` access-token issuance.
4. Logout token invalidation and, if refresh tokens are adopted, stateful
   refresh-token storage, hashing, rotation, and reuse detection.

Access tokens should remain short-lived. Refresh tokens must not be implemented
as interchangeable long-lived access tokens.
