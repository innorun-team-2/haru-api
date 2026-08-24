package org.example.haruapi.comment.service;

import org.example.haruapi.comment.dto.CommentGetResponse;
import org.example.haruapi.comment.entity.Comment;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.comment.dto.CommentCreateRequest;
import org.example.haruapi.comment.dto.CommentCreateResponse;
import org.example.haruapi.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
}