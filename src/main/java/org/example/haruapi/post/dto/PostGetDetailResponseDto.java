package org.example.haruapi.post.dto;

import lombok.Getter;
import org.example.haruapi.image.entity.Image;
import org.example.haruapi.post.entity.Post;

import java.time.LocalDateTime;

@Getter
public class PostGetDetailResponseDto {

    private final String title;
    private final String nickname;
    private final String content;
    private final LocalDateTime createdAt;
    private final String image;

    private PostGetDetailResponseDto(String title, String nickname, String content, LocalDateTime createdAt, String image) {
        this.title = title;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = createdAt;
        this.image = image;
    }

    public static PostGetDetailResponseDto from(Post post, Image image) {
        return new PostGetDetailResponseDto(
                post.getTitle(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getCreatedAt(),
                image.getPath()
        );
    }
}
