package org.example.haruapi.user;

import org.example.haruapi.global.security.jwt.JwtTokenProvider;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.cloud.aws.s3.bucket=test-bucket",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class UserUpdateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void updatesNicknameAndPasswordForAuthenticatedUser() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Before!"
        );

        performUpdate(user, Map.of(
                "nickname", "After!",
                "password", "Changed123!"
        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error").doesNotExist());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo("After!");
        assertThat(passwordEncoder.matches(
                "Changed123!",
                updatedUser.getPassword()
        )).isTrue();
    }

    @Test
    void updatesOnlyNickname() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Before!"
        );

        performUpdate(user, Map.of("nickname", "After"))
                .andExpect(status().isOk());

        assertThat(user.getNickname()).isEqualTo("After");
        assertThat(passwordEncoder.matches(
                "Password123!",
                user.getPassword()
        )).isTrue();
    }

    @Test
    void updatesOnlyPassword() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Member!"
        );

        performUpdate(user, Map.of("password", "Changed123!"))
                .andExpect(status().isOk());

        assertThat(user.getNickname()).isEqualTo("Member!");
        assertThat(passwordEncoder.matches(
                "Changed123!",
                user.getPassword()
        )).isTrue();
    }

    @Test
    void rejectsDuplicatedNicknameIgnoringCase() throws Exception {
        createUser("first@example.com", "Password123!", "Taken!");
        User requester = createUser(
                "second@example.com",
                "Password123!",
                "Other!"
        );

        performUpdate(requester, Map.of("nickname", "taken!"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code")
                        .value("USER_NICKNAME_ALREADY_EXISTS"));

        assertThat(requester.getNickname()).isEqualTo("Other!");
    }

    @Test
    void rejectsRequestWithoutEditableFields() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Member!"
        );

        performUpdate(user, Map.of())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsInvalidNicknameAndPassword() throws Exception {
        User user = createUser(
                "member@example.com",
                "Password123!",
                "Member!"
        );

        performUpdate(user, Map.of(
                "nickname", "한글닉네임!",
                "password", "Password1!가"
        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("nickname", "After!")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void returnsNotFoundWhenTokenUserDoesNotExist() throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(999L, List.of("USER"))
                .value();

        mockMvc.perform(put("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("nickname", "After!")
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
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

    private org.springframework.test.web.servlet.ResultActions performUpdate(
            User user,
            Map<String, String> request
    ) throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(user.getId(), List.of("USER"))
                .value();

        return mockMvc.perform(put("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}
