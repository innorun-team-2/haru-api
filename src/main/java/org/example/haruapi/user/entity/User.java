package org.example.haruapi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.haruapi.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)



public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private User(String email, String password, String nickname, LocalDateTime deletedAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.deletedAt = deletedAt;

    }







}
