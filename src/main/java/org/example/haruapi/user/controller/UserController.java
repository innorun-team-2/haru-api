package org.example.haruapi.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.haruapi.global.dto.ApiResponse;
import org.example.haruapi.user.dto.request.UserRegisterRequest;
import org.example.haruapi.user.dto.response.UserRegisterResponse;
import org.example.haruapi.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
