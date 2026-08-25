package org.example.haruapi.user.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static org.example.haruapi.user.validation.UserValidationRules.NICKNAME_PATTERN;
import static org.example.haruapi.user.validation.UserValidationRules.PASSWORD_PATTERN;

public record UserUpdateRequest(
        @Size(
                min = 8,
                max = 64,
                message = "비밀번호는 8자 이상 64자 이내로 입력해주세요."
        )
        @Pattern(
                regexp = PASSWORD_PATTERN,
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 포함해야 합니다."
        )
        String password,

        @Size(max = 10, message = "닉네임은 10자 이내로 입력해주세요.")
        @Pattern(
                regexp = NICKNAME_PATTERN,
                message = "닉네임은 영문을 반드시 포함하고, 영문과 특수문자만 사용할 수 있습니다."
        )
        String nickname
) {
    public UserUpdateRequest {
        nickname = nickname == null ? null : nickname.trim();
    }

    @AssertTrue(message = "비밀번호와 닉네임 중 하나 이상을 입력해주세요.")
    public boolean isUpdateRequested() {
        return password != null || nickname != null;
    }
}
