package org.example.haruapi.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.user.entity.User;

/**
 * 게시글 생성 요청 Dto
 */
@Getter
public class PostCreateRequestDto {

    @NotBlank(message = "제목은 필수로 작성해야 합니다.")
    @Size(max = 100, message = "제목은 100자 이내로 작성해야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수로 작성해야 합니다.")
    @Size(max = 200, message = "내용은 200자 이내로 작성해야 합니다.")
    private String content;
}
