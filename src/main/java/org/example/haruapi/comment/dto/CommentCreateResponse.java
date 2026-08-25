package org.example.haruapi.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentCreateResponse {

    private final Long id;
    private final Long postId;
    private final String content;
    private final Long userId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public CommentCreateResponse(Long id, Long postId, String content, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
