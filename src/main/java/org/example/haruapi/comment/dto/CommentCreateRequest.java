package org.example.haruapi.comment.dto;

import lombok.Getter;

@Getter
public class CommentCreateRequest {

    private String content;

    // 테스트용 생성자
    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
