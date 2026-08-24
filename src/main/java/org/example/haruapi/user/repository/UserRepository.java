package org.example.haruapi.user.repository;

import org.example.haruapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNicknameIgnoreCase(String nickname);

    Optional<User> findByEmail(String email);
}
