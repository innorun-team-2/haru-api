package org.example.haruapi.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> save(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.ok();
    }
}
