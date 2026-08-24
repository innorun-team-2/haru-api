package org.example.haruapi.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 255, message = "이메일은 255자 이내로 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(
                min = 8,
                max = 64,
                message = "비밀번호는 8자 이상 64자 이내로 입력해주세요."
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\p{Punct})"
                        + "[A-Za-z\\d\\p{Punct}]+$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 10, message = "닉네임은 10자 이내로 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\p{Punct})[A-Za-z\\p{Punct}]+$",
                message = "닉네임은 영문과 특수문자를 각각 포함해야 합니다."
        )
        String nickname
) {
    public UserRegisterRequest {
        email = email == null ? null : email.trim();
        nickname = nickname == null ? null : nickname.trim();
    }
}
