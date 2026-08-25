package org.example.haruapi.post.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.repository.CommentRepository;
import org.example.haruapi.global.s3.service.S3Service;
import org.example.haruapi.image.entity.Image;
import org.example.haruapi.image.repository.ImageRepository;
import org.example.haruapi.post.dto.*;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.post.exception.ImageRequiredException;
import org.example.haruapi.post.exception.PostForbiddenException;
import org.example.haruapi.post.exception.PostNotFoundException;
import org.example.haruapi.post.repository.PostRepository;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

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

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<PostGetAllResponseDto> getAll(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByDeletedAtIsNull(pageable); // 요청한 페이지만큼 게시글 가져옴 (기본 10개)
        List<Post> posts = postPage.getContent(); // 게시글 추출

        // 게시글이 없다면 빈 페이지 객체 반환
        if (posts.isEmpty()) {
            return Page.empty(pageable);
        }

        // 조회된 10개의 게시글에서 postId만 뽑아옴
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        // 10개의 이미지를 한 번에 조회, map으로 변환
        List<Image> images = imageRepository.findByPostIdIn(postIds);
        Map<Long, Image> imageMap = images.stream()
                .collect(Collectors.toMap(image -> image.getPost().getId(), image -> image));

        // 10개의 댓글 통계(group by)를 한 번에 조회, map으로 변환
        List<Object[]> commentCounts = commentRepository.countByPostIdIn(postIds);
        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));

        // dto로 조립
        List<PostGetAllResponseDto> dtoList = posts.stream()
                .map(post -> {
                    Long postId = post.getId();
                    Image image = imageMap.get(postId);
                    Long count = commentCountMap.getOrDefault(postId, 0L);

                    return PostGetAllResponseDto.from(post, image, count);
                }).toList();

        return new PageImpl<>(dtoList, pageable, postPage.getTotalElements());
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public List<PostGetDetailResponseDto> getDetail(Long postId) {
        // 포스트가 존재하는지
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));

        // 포스트로 이미지 가져오기
        Image image = imageRepository.findByPost(post);

        return List.of(PostGetDetailResponseDto.from(post, image));
    }

    // 내 게시글 조회
    @Transactional(readOnly = true)
    public List<PostGetMyResponseDto> getMy(Long userId) {
        List<Post> posts = postRepository.findAllByUserIdAndDeletedAtIsNull(userId);

        return posts.stream()
                .map(post -> {
                    Image image = imageRepository.findByPost(post);
                    return PostGetMyResponseDto.from(post, image);
                }).toList();
    }

    // 게시글 수정
    public List<PostUpdateResponseDto> update(Long userId, Long postId, PostUpdateRequestDto request) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));

        // 권한 검증 (NPE 방지)
        if (post.getUser() == null || !Objects.equals(post.getUser().getId(), userId)) {
            throw new PostForbiddenException("게시글 수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        return List.of(PostUpdateResponseDto.from(post));
    }

    // 게시글 삭제
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));

        // 권한 검증 (NPE 방지)
        if (post.getUser() == null || !Objects.equals(post.getUser().getId(), userId)) {
            throw new PostForbiddenException("게시글 삭제 권한이 없습니다.");
        }

        post.delete();
    }
}
