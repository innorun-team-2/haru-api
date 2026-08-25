package org.example.haruapi.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentCreateRequest {

    @Size(max = 100, message = "내용은 100자 이내로 입력해주세요.")
    private String content;

    // 테스트용 생성자
    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
