package org.example.haruapi.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.haruapi.global.dto.ApiResponse;
import org.example.haruapi.user.dto.request.UserRegisterRequest;
import org.example.haruapi.user.dto.request.UserUpdateRequest;
import org.example.haruapi.user.dto.response.UserInfoResponse;
import org.example.haruapi.user.dto.response.UserRegisterResponse;
import org.example.haruapi.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        UserRegisterResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(List.of(response)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserInfoResponse response = userService.getCurrentUser(
                Long.valueOf(jwt.getSubject())
        );
        return ResponseEntity.ok(ApiResponse.success(List.of(response)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> update(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.update(Long.valueOf(jwt.getSubject()), request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.withdraw(Long.valueOf(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success());
    }
}
