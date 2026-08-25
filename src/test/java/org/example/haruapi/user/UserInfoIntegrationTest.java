package org.example.haruapi.user;

import org.example.haruapi.global.security.jwt.JwtTokenProvider;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.cloud.aws.s3.bucket=test-bucket",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class UserInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getsAuthenticatedUserInfoWithoutSensitiveFields() throws Exception {
        User user = createUser();

        performGetCurrentUser(user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(user.getId()))
                .andExpect(jsonPath("$.data[0].email")
                        .value("member@example.com"))
                .andExpect(jsonPath("$.data[0].nickname").value("Member!"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[0].deletedAt").doesNotExist())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void returnsNotFoundWhenTokenUserDoesNotExist() throws Exception {
        performGetCurrentUser(999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    @Test
    void returnsNotFoundForWithdrawnUser() throws Exception {
        User user = createUser();
        user.withdraw();
        userRepository.flush();

        performGetCurrentUser(user.getId())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    private User createUser() {
        return userRepository.saveAndFlush(User.create(
                "member@example.com",
                passwordEncoder.encode("Password123!"),
                "Member!"
        ));
    }

    private org.springframework.test.web.servlet.ResultActions performGetCurrentUser(
            Long userId
    ) throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(userId, List.of("USER"))
                .value();

        return mockMvc.perform(get("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }
}
