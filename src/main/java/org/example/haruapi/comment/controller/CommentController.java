package org.example.haruapi.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.*;
import org.example.haruapi.comment.service.CommentService;
import org.example.haruapi.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> save(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                ApiResponse.success(
                        commentService.save(
                                userId,
                                postId,
                                request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CommentGetResponse>> getAll(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        commentService.getAll(postId)));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentUpdateResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                ApiResponse.success(
                        commentService.update(
                                userId,
                                postId,
                                commentId,
                                request)));
    }
}