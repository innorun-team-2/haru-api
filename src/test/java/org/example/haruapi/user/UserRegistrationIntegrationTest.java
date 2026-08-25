package org.example.haruapi.user;

import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.entity.UserRole;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

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
class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registersUserWithNormalizedEmailAndEncodedPassword() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "  NewUser@Example.COM  ",
                                "Password123!",
                                "  Haru!  "
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").isNumber())
                .andExpect(jsonPath("$.error").doesNotExist());

        User savedUser = userRepository.findByEmail("newuser@example.com")
                .orElseThrow();
        assertThat(savedUser.getNickname()).isEqualTo("Haru!");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.getPassword()).isNotEqualTo("Password123!");
        assertThat(passwordEncoder.matches(
                "Password123!",
                savedUser.getPassword()
        )).isTrue();
    }

    @Test
    void rejectsDuplicatedNormalizedEmail() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "member@example.com",
                                "Password123!",
                                "First!"
                        )))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                " MEMBER@EXAMPLE.COM ",
                                "Another123!",
                                "Second!"
                        )))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code")
                        .value("USER_EMAIL_ALREADY_EXISTS"));

        assertThat(userRepository.count()).isOne();
    }

    @Test
    void rejectsInvalidRequestWithCommonErrorResponse() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "not-an-email",
                                "short",
                                ""
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void rejectsNonAsciiPassword() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "member@example.com",
                                "Password1!가",
                                "Member!"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void registersNicknameWithoutSpecialCharacter() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "member@example.com",
                                "Password123!",
                                "Haru"
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(userRepository.count()).isOne();
    }

    @Test
    void rejectsNicknameWithoutEnglishCharacter() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "member@example.com",
                                "Password123!",
                                "!!!"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void rejectsNicknameContainingNumber() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "member@example.com",
                                "Password123!",
                                "Haru123!"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void rejectsDuplicatedNicknameIgnoringCase() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "first@example.com",
                                "Password123!",
                                "Haru!"
                        )))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(
                                "second@example.com",
                                "Another123!",
                                "haru!"
                        )))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code")
                        .value("USER_NICKNAME_ALREADY_EXISTS"));

        assertThat(userRepository.count()).isOne();
    }

    private String requestJson(
            String email,
            String password,
            String nickname
    ) {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password,
                "nickname", nickname
        ));
    }
}
