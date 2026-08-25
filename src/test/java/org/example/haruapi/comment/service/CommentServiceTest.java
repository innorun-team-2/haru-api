package org.example.haruapi.comment.service;

import org.example.haruapi.comment.dto.*;
import org.example.haruapi.comment.entity.Comment;
import org.example.haruapi.comment.repository.CommentRepository;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.post.repository.PostRepository;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 댓글_생성_성공() {
        // given
        Long userId = 1L;
        Long postId = 1L;

        User user = mock(User.class);
        Post post = mock(Post.class);

        given(user.getId())
                .willReturn(userId);

        given(post.getId())
                .willReturn(postId);

        given(userRepository.findByIdAndDeletedAtIsNull(userId))
                .willReturn(Optional.of(user));

        given(postRepository.findByIdAndDeletedAtIsNull(postId))
                .willReturn(Optional.of(post));

        CommentCreateRequest request =
                new CommentCreateRequest("테스트 댓글입니다.");

        // when
        List<CommentCreateResponse> result =
                commentService.save(
                        userId,
                        postId,
                        request
                );

        // then
        assertThat(result).hasSize(1);

        assertThat(result.get(0).getContent())
                .isEqualTo("테스트 댓글입니다.");

        assertThat(result.get(0).getPostId())
                .isEqualTo(postId);

        assertThat(result.get(0).getUserId())
                .isEqualTo(userId);

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
        verify(postRepository).findByIdAndDeletedAtIsNull(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void 댓글_전체_조회_성공() {
        // given
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        given(user1.getId()).willReturn(1L);
        given(user1.getNickname()).willReturn("테스트유저1");

        given(user2.getId()).willReturn(2L);
        given(user2.getNickname()).willReturn("테스트유저2");

        Post post = mock(Post.class);

        given(postRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(post));

        Comment comment1 = mock(Comment.class);
        Comment comment2 = mock(Comment.class);

        given(comment1.getId()).willReturn(1L);
        given(comment1.getContent()).willReturn("첫 번째 댓글");
        given(comment1.getUser()).willReturn(user1);

        given(comment2.getId()).willReturn(2L);
        given(comment2.getContent()).willReturn("두 번째 댓글");
        given(comment2.getUser()).willReturn(user2);

        given(commentRepository.findByPostIdAndDeletedAtIsNull(1L))
                .willReturn(List.of(comment1, comment2));

        // when
        List<CommentGetResponse> result =
                commentService.getAll(1L);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 댓글_내용_수정_성공() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        Post post = mock(Post.class);

        User user = mock(User.class);
        given(user.getId()).willReturn(userId);
        given(user.getNickname()).willReturn("테스트유저");

        Comment comment = mock(Comment.class);
        given(comment.getId()).willReturn(commentId);
        given(comment.getUser()).willReturn(user);
        given(comment.getContent()).willReturn("수정된 댓글입니다.");

        given(postRepository.findByIdAndDeletedAtIsNull(postId))
                .willReturn(Optional.of(post));

        given(commentRepository.findByIdAndDeletedAtIsNull(commentId))
                .willReturn(Optional.of(comment));

        CommentUpdateRequest request =
                new CommentUpdateRequest("수정된 댓글입니다.");

        // when
        List<CommentUpdateResponse> result =
                commentService.update(
                        userId,
                        postId,
                        commentId,
                        request
                );

        // then
        assertThat(result).hasSize(1);

        assertThat(result.get(0).getId())
                .isEqualTo(commentId);

        assertThat(result.get(0).getContent())
                .isEqualTo("수정된 댓글입니다.");

        assertThat(result.get(0).getUserId())
                .isEqualTo(userId);

        assertThat(result.get(0).getUserNickname())
                .isEqualTo("테스트유저");

        verify(postRepository).findByIdAndDeletedAtIsNull(postId);
        verify(commentRepository).findByIdAndDeletedAtIsNull(commentId);
        verify(comment).update("수정된 댓글입니다.");
    }

    @Test
    void 댓글_삭제_성공() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        User user = mock(User.class);
        Post post = mock(Post.class);
        Comment comment = mock(Comment.class);

        given(user.getId()).willReturn(userId);

        given(postRepository.findByIdAndDeletedAtIsNull(postId))
                .willReturn(Optional.of(post));

        given(commentRepository.findByIdAndDeletedAtIsNull(commentId))
                .willReturn(Optional.of(comment));

        given(comment.getUser())
                .willReturn(user);

        // when
        commentService.delete(
                userId,
                postId,
                commentId
        );

        // then
        verify(postRepository).findByIdAndDeletedAtIsNull(postId);
        verify(commentRepository).findByIdAndDeletedAtIsNull(commentId);
        verify(comment).updateDeletedAt();
    }
}