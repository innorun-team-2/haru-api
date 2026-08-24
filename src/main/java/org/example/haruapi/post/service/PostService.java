package org.example.haruapi.post.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.global.s3.service.S3Service;
import org.example.haruapi.image.entity.Image;
import org.example.haruapi.image.repository.ImageRepository;
import org.example.haruapi.post.dto.PostCreateRequestDto;
import org.example.haruapi.post.dto.PostCreateResponseDto;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.post.exception.ImageRequiredException;
import org.example.haruapi.post.repository.PostRepository;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public PostCreateResponseDto create(Long userId, PostCreateRequestDto requestDto, MultipartFile image) {
        // 유저 조회 검증 로직 (수정 예정)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이미지가 비어있다면 예외 처리
        if (ObjectUtils.isEmpty(image)) {
            throw new ImageRequiredException("게시글 생성시 이미지 첨부는 필수입니다.");
        }

        // 게시글 저장
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();
        Post savePost = postRepository.save(post);

        // S3 URL 반환
        String imagePath = s3Service.upload(image);

        // 이미지 저장
        Image imageEntity = Image.builder()
                .path(imagePath)
                .post(savePost)
                .build();
        imageRepository.save(imageEntity);

        return PostCreateResponseDto.from(savePost);
    }
}
