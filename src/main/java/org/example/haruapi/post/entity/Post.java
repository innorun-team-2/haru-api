package org.example.haruapi.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.haruapi.global.entity.BaseEntity;
import org.example.haruapi.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String content;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private LocalDateTime deletedAt;

    @Builder
    private Post(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}
