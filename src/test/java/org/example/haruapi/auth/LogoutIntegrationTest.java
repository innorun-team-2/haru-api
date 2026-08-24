package org.example.haruapi.auth;

import org.example.haruapi.global.security.jwt.JwtTokenProvider;
import org.example.haruapi.global.security.jwt.revocation.RevokedAccessTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.cloud.aws.s3.bucket=test-bucket",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class LogoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RevokedAccessTokenRepository revokedAccessTokenRepository;

    @Test
    void logsOutAndReturnsCommonEmptySuccessResponse() throws Exception {
        String token = issueAccessToken();

        performLogout(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error").doesNotExist());

        assertThat(revokedAccessTokenRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsReuseOfLoggedOutAccessToken() throws Exception {
        String token = issueAccessToken();

        performLogout(token).andExpect(status().isOk());

        performLogout(token)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void logoutRequiresBearerToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void revokingOneTokenDoesNotRevokeAnotherToken() throws Exception {
        String firstToken = issueAccessToken();
        String secondToken = issueAccessToken();

        performLogout(firstToken).andExpect(status().isOk());
        performLogout(secondToken).andExpect(status().isOk());

        assertThat(revokedAccessTokenRepository.count()).isEqualTo(2);
    }

    private String issueAccessToken() {
        return jwtTokenProvider.issueAccessToken(1L, List.of("USER")).value();
    }

    private org.springframework.test.web.servlet.ResultActions performLogout(
            String token
    ) throws Exception {
        return mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }
}
