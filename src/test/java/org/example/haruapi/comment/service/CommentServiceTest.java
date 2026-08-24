package org.example.haruapi.comment.service;

import org.example.haruapi.comment.dto.CommentGetResponse;
import org.example.haruapi.comment.dto.CommentUpdateRequest;
import org.example.haruapi.comment.dto.CommentUpdateResponse;
import org.example.haruapi.comment.entity.Comment;
import org.example.haruapi.comment.repository.CommentRepository;
import org.example.haruapi.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 댓글_생성_성공() {
        // given
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", "1")
                .build();

        CommentCreateRequest request =
                new CommentCreateRequest("테스트 댓글입니다.");

        // when
        List<CommentCreateResponse> result =
                commentService.save(jwt, 1L, request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent())
                .isEqualTo("테스트 댓글입니다.");
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
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", "1")
                .build();

        CommentUpdateRequest request =
                new CommentUpdateRequest("수정된 댓글입니다.");

        User user = mock(User.class);
        given(user.getId()).willReturn(1L);
        given(user.getNickname()).willReturn("테스트유저");

        Comment comment = mock(Comment.class);

        given(comment.getId()).willReturn(1L);
        given(comment.getContent()).willReturn("수정된 댓글입니다.");
        given(comment.getUser()).willReturn(user);

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        // when
        List<CommentUpdateResponse> result =
                commentService.update(jwt, 1L, 1L, request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent())
                .isEqualTo("수정된 댓글입니다.");
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getUserNickname())
                .isEqualTo("테스트유저");

        verify(comment).update("수정된 댓글입니다.");
    }

    @Test
    void 댓글_삭제_성공() {
        // given
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", "1")
                .build();

        User user = mock(User.class);
        given(user.getId()).willReturn(1L);

        Comment comment = mock(Comment.class);
        given(comment.getUser()).willReturn(user);

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        // when
        commentService.delete(jwt, 1L, 1L);

        // then
        verify(comment).updateDeletedAt();
    }
}