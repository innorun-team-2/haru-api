package org.example.haruapi.user.dto.response;

import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.entity.UserRole;

public record UserInfoResponse(
        Long userId,
        String email,
        String nickname,
        UserRole role
) {

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );
    }
}
