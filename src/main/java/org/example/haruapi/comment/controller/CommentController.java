package org.example.haruapi.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.service.CommentService;
import org.example.haruapi.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentCreateResponse>> save(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        commentService.save(
                                jwt,
                                postId,
                                request)));
    }
}
