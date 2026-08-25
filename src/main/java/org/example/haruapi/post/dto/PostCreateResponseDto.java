package org.example.haruapi.post.dto;

import lombok.Getter;
import org.example.haruapi.post.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 생성 응답 Dto
 */

@Getter
public class PostCreateResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
//    private String imageUrl;

    public PostCreateResponseDto(Long id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static PostCreateResponseDto from(Post post) {
        return new PostCreateResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt()
        );
    }
}
