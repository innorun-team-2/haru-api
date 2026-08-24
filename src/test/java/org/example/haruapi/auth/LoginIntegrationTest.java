package org.example.haruapi.auth;

import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.cloud.aws.s3.bucket=test-bucket",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void logsInWithNormalizedEmailAndIssuesAccessToken() throws Exception {
        User user = createUser("member@example.com", "Password123!", "Member!");

        MvcResult result = performLogin(
                "  MEMBER@EXAMPLE.COM  ",
                "Password123!"
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data[0].accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data[0].expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString("Password123!")
                        )
                ))
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );
        String token = response.at("/data/0/accessToken").asString();
        Jwt jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("USER");
        assertThat(jwt.getExpiresAt()).isNotNull();
    }

    @Test
    void rejectsWrongPasswordWithGenericCredentialsError() throws Exception {
        createUser("member@example.com", "Password123!", "Member!");

        performLogin("member@example.com", "WrongPassword1!")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.error.message")
                        .value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void rejectsUnknownEmailWithSameCredentialsError() throws Exception {
        performLogin("unknown@example.com", "Password123!")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.error.message")
                        .value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void rejectsWithdrawnUserWithSameCredentialsError() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Member!"
        );
        user.withdraw();
        userRepository.flush();

        performLogin("member@example.com", "Password123!")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void rejectsInvalidLoginRequestWithCommonErrorResponse() throws Exception {
        performLogin("not-an-email", "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    private User createUser(
            String email,
            String rawPassword,
            String nickname
    ) {
        return userRepository.saveAndFlush(User.create(
                email,
                passwordEncoder.encode(rawPassword),
                nickname
        ));
    }

    private org.springframework.test.web.servlet.ResultActions performLogin(
            String email,
            String password
    ) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password
                ))));
    }
}
