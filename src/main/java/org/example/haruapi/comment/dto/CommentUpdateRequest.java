package org.example.haruapi.comment.dto;

import lombok.Getter;

@Getter
public class CommentUpdateRequest {

    private String content;

    // 테스트용 생성자
    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
