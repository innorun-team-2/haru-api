package org.example.haruapi.post.dto;

import lombok.Getter;
import org.example.haruapi.image.entity.Image;
import org.example.haruapi.post.entity.Post;

import java.time.LocalDateTime;

@Getter
public class PostGetMyResponseDto {

    private final Long postId;
    private final String title;
    private final String nickname;
    private final String content;
    private final LocalDateTime createdAt;
    private final String image;

    private PostGetMyResponseDto(Long postId, String title, String nickname, String content, LocalDateTime createdAt, String image) {
        this.postId = postId;
        this.title = title;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = createdAt;
        this.image = image;
    }

    public static PostGetMyResponseDto from(Post post, Image image) {
        return new PostGetMyResponseDto(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getCreatedAt(),
                image.getPath()
        );
    }
}
