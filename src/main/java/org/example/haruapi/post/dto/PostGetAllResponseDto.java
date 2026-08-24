package org.example.haruapi.post.dto;

import lombok.Getter;
import org.example.haruapi.image.entity.Image;
import org.example.haruapi.post.entity.Post;

import java.time.LocalDateTime;

@Getter
public class PostGetAllResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final String image;
    private final String nickname;
    private final Long commentCount;

    private PostGetAllResponseDto(Long id, String title, String content, LocalDateTime createdAt, String image, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.image = image;
        this.nickname = nickname;
        this.commentCount = 0L; // 임시로 0으로 초기화
    }

    public static PostGetAllResponseDto from(Post post, Image image) {
        return new PostGetAllResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                image.getPath(),
                post.getUser().getNickname()
                // 댓글 개수 초기화 필드 추가 예정
        );
    }
}
