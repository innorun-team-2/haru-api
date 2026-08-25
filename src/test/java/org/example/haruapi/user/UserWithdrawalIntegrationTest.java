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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.cloud.aws.s3.bucket=test-bucket",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class UserWithdrawalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void softlyWithdrawsAuthenticatedUser() throws Exception {
        User user = createUser("member@example.com", "Member!");

        performWithdrawal(user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error").doesNotExist());

        User withdrawnUser = userRepository.findById(user.getId())
                .orElseThrow();
        assertThat(withdrawnUser.getDeletedAt()).isNotNull();
        assertThat(userRepository.findByIdAndDeletedAtIsNull(user.getId()))
                .isEmpty();
    }

    @Test
    void withdrawalDoesNotDeleteDatabaseRowOrOtherUser() throws Exception {
        User requester = createUser("requester@example.com", "Requester!");
        User otherUser = createUser("other@example.com", "Other!");

        performWithdrawal(requester.getId())
                .andExpect(status().isOk());

        assertThat(userRepository.findById(requester.getId())).isPresent();
        assertThat(userRepository.findByIdAndDeletedAtIsNull(otherUser.getId()))
                .isPresent();
    }

    @Test
    void rejectsRepeatedWithdrawal() throws Exception {
        User user = createUser("member@example.com", "Member!");

        performWithdrawal(user.getId())
                .andExpect(status().isOk());

        performWithdrawal(user.getId())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code")
                        .value("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void returnsNotFoundWhenTokenUserDoesNotExist() throws Exception {
        performWithdrawal(999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    private User createUser(String email, String nickname) {
        return userRepository.saveAndFlush(User.create(
                email,
                passwordEncoder.encode("Password123!"),
                nickname
        ));
    }

    private org.springframework.test.web.servlet.ResultActions performWithdrawal(
            Long userId
    ) throws Exception {
        String token = jwtTokenProvider
                .issueAccessToken(userId, List.of("USER"))
                .value();

        return mockMvc.perform(delete("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }
}
