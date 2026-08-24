package org.example.haruapi.user.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.user.dto.request.UserRegisterRequest;
import org.example.haruapi.user.dto.response.UserRegisterResponse;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.exception.DuplicateEmailException;
import org.example.haruapi.user.exception.DuplicateNicknameException;
import org.example.haruapi.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        String email = normalizeEmail(request.email());
        String nickname = request.nickname().trim();

        validateEmailAvailable(email);
        validateNicknameAvailable(nickname);

        User user = User.create(
                email,
                passwordEncoder.encode(request.password()),
                nickname
        );

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return new UserRegisterResponse(savedUser.getId());
        } catch (DataIntegrityViolationException exception) {
            if (isNicknameConstraintViolation(exception)) {
                throw new DuplicateNicknameException();
            }
            throw new DuplicateEmailException();
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validateEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    private void validateNicknameAvailable(String nickname) {
        if (userRepository.existsByNicknameIgnoreCase(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

    private boolean isNicknameConstraintViolation(
            DataIntegrityViolationException exception
    ) {
        String message = exception.getMostSpecificCause().getMessage();
        return message != null
                && message.toLowerCase(Locale.ROOT)
                .contains("uk_users_nickname");
    }
}
