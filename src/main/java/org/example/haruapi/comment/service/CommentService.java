package org.example.haruapi.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.entity.Comment;
import org.example.haruapi.comment.repository.CommentRepository;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public List<CommentCreateResponse> save(Long userId, Long postId, CommentCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 사용자입니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("존재하지 않는 포스트입니다.")
        );

        Comment comment = new Comment(
                post,
                request.getContent(),
                user
        );

        commentRepository.save(comment);

        return List.of(new CommentCreateResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        ));
    }
}
