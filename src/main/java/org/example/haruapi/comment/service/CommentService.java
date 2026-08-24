package org.example.haruapi.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.repository.CommentRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
//    private final UserRepository userRepository;
//    private final PostRepository postRepository;

    @Transactional
    public List<CommentCreateResponse> save(Jwt jwt, Long postId, CommentCreateRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new UserNotFoundException("존재하지 않는 사용자입니다.")
//        );

//        Post post = postRepository.findById(postId).orElseThrow(
//                () -> new PostNotFoundException("존재하지 않는 포스트입니다.")
//        );

//        Comment comment = new Comment(
//                post,
//                request.getContent(),
//                user
//        );
//
//        commentRepository.save(comment);

//        return List.of(new CommentCreateResponse(
//                comment.getId(),
//                comment.getPost().getId(),
//                comment.getContent(),
//                comment.getUser().getId(),
//                comment.getCreatedAt(),
//                comment.getUpdatedAt(),
//                comment.getDeletedAt()
//        ));
//
        return List.of(
                new CommentCreateResponse(
                        1L,
                        1L,
                        "테스트 댓글입니다.",
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        null
                )
        );
    }
}
