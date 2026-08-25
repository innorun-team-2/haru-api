package org.example.haruapi.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.haruapi.global.dto.ApiResponse;
import org.example.haruapi.post.dto.*;
import org.example.haruapi.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponseDto>> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart(value = "requestDto") @Valid PostCreateRequestDto requestDto,
            @RequestPart(value = "image") MultipartFile image
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        PostCreateResponseDto responseDto = postService.create(userId, requestDto, image);

        return ResponseEntity.ok(ApiResponse.success(List.of(responseDto)));
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PostGetAllResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PostGetAllResponseDto> postPage = postService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(postPage.getContent()));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostGetDetailResponseDto>> getDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(postService.getDetail(postId)));
    }

    // 내 게시글 전체 조회
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PostGetMyResponseDto>> getMy(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(ApiResponse.success(postService.getMy(userId)));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponseDto>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequestDto request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(postService.update(userId, postId, request)));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        postService.delete(userId, postId);

        return ResponseEntity.ok(ApiResponse.success());
    }
}
