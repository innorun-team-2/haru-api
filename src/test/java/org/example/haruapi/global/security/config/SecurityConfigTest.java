package org.example.haruapi.global.security.config;

import org.example.haruapi.global.security.handler.RestAccessDeniedHandler;
import org.example.haruapi.global.security.handler.RestAuthenticationEntryPoint;
import org.example.haruapi.global.security.jwt.JwtConfig;
import org.example.haruapi.global.security.jwt.JwtTokenProvider;
import org.example.haruapi.global.security.jwt.revocation.JwtRevocationValidator;
import org.example.haruapi.global.security.jwt.revocation.RevokedAccessTokenRepository;
import org.example.haruapi.global.security.jwt.revocation.TokenRevocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SecurityConfigTest.TestController.class,
        properties = "security.jwt.secret-key="
                + "VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM="
)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
        JwtTokenProvider.class,
        JwtRevocationValidator.class,
        TokenRevocationService.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        SecurityConfigTest.TestController.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RevokedAccessTokenRepository revokedAccessTokenRepository;

    @Test
    void loginEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void signupEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUserEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointReturnsCommonUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/private"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void validBearerTokenCanAccessProtectedEndpoint() throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(1L, List.of("USER"))
                .value();

        mockMvc.perform(get("/api/private")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userRoleCannotAccessAdminEndpoint() throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(1L, List.of("USER"))
                .value();

        mockMvc.perform(get("/api/admin/sample")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"));
    }

    @RestController
    static class TestController {

        @PostMapping("/api/auth/login")
        String login() {
            return "login";
        }

        @PostMapping("/api/users")
        String signup() {
            return "signup";
        }

        @PutMapping("/api/users/me")
        String updateUser() {
            return "update-user";
        }

        @DeleteMapping("/api/users/me")
        String deleteUser() {
            return "delete-user";
        }

        @PostMapping("/api/auth/logout")
        String logout() {
            return "logout";
        }

        @GetMapping("/api/private")
        String privateEndpoint() {
            return "private";
        }

        @GetMapping("/api/admin/sample")
        String adminEndpoint() {
            return "admin";
        }
    }
}
