package org.example.haruapi.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.haruapi.global.entity.BaseEntity;
import org.example.haruapi.post.entity.Post;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "images")
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime deletedAt;

    @Builder
    private Image(String path, Post post) {
        this.path = path;
        this.post = post;
    }

    // 소프트 딜리트
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
