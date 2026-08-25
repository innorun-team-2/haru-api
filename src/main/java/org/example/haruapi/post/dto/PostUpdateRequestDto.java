package org.example.haruapi.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateRequestDto {

    @NotBlank(message = "제목 수정 시 필수로 작성해야 합니다.")
    @Size(max = 100, message = "제목은 100자 이내로 작성해야 합니다.")
    private String title;
    @NotBlank(message = "내용 수정 시 필수로 작성해야 합니다.")
    @Size(max = 200, message = "내용은 200자 이내로 작성해야 합니다.")
    private String content;

    @Builder
    public PostUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
