package org.example.haruapi.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.entity.Comment;
import org.example.haruapi.comment.repository.CommentRepository;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.post.repository.PostRepository;
import org.example.haruapi.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentCreateResponse save(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 포스트입니다.")
        );

        User user = new User();

        Comment comment = new Comment(
                post,
                request.getContent(),
                user
        );

        commentRepository.save(comment);

        return new CommentCreateResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }
}
