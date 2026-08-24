package org.example.haruapi.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.haruapi.global.dto.ApiResponse;
import org.example.haruapi.post.dto.PostCreateRequestDto;
import org.example.haruapi.post.dto.PostCreateResponseDto;
import org.example.haruapi.post.service.PostService;
import org.springframework.http.ResponseEntity;
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
//             @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(name = "userId") Long userId, // 임시 유저 id
            @RequestPart(value = "requestDto") @Valid PostCreateRequestDto requestDto,
            @RequestPart(value= "image") MultipartFile image
    ) {
        PostCreateResponseDto responseDto = postService.create(userId, requestDto, image);

        return ResponseEntity.ok(ApiResponse.success(List.of(responseDto)));
    }
}
