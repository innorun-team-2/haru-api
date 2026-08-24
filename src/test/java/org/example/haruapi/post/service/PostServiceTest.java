package org.example.haruapi.post.service;

import org.example.haruapi.image.entity.Image;
import org.example.haruapi.image.repository.ImageRepository;
import org.example.haruapi.post.dto.PostGetAllResponseDto;
import org.example.haruapi.post.dto.PostGetDetailResponseDto;
import org.example.haruapi.post.dto.PostGetMyResponseDto;
import org.example.haruapi.post.entity.Post;
import org.example.haruapi.post.exception.PostNotFoundException;
import org.example.haruapi.post.repository.PostRepository;
import org.example.haruapi.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void 게시글_전체_조회가_성공한다() {
        // given
        User user = User.create("test@test.com", "password", "닉네임");
        User user2 = User.create("test@test.com2", "password2", "닉네임2");
        Post post = Post.builder()
                .title("전체조회 제목")
                .content("내용")
                .user(user)
                .build();
        Post post2 = Post.builder()
                .title("전체조회 제목22")
                .content("내용22")
                .user(user2)
                .build();

        Image image = Image.builder()
                .path("image.jpg")
                .post(post)
                .build();

        Image image2 = Image.builder()
                .path("image.jpg2")
                .post(post2)
                .build();

        given(postRepository.findAllByDeletedAtIsNull()).willReturn(List.of(post, post2));

        given(imageRepository.findByPost(post)).willReturn(image);
        given(imageRepository.findByPost(post2)).willReturn(image2);

        // when
        List<PostGetAllResponseDto> result = postService.getAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("전체조회 제목");
    }

    @Test
    void 게시글_상세조회_가_성공한다() {
        //given

        User user = User.create("test@test.com", "password123", "작성자닉네임");
        Long postId = 1L;
        Post post = Post.builder()
                .title("상세 조회 제목")
                .content("상세 조회 내용")
                .user(user)
                .build();

        Image image = Image.builder()
                .path("test-image-url.jpg")
                .post(post)
                .build();

        given(postRepository.findByIdAndDeletedAtIsNull(postId)).willReturn(Optional.of(post));
        given(imageRepository.findByPost(post)).willReturn(image);

        //when
        List<PostGetDetailResponseDto> result = postService.getDetail(postId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("상세 조회 제목");
    }

    @Test
    void 게시글_상세조회_실패_시_예외발생한다() {
        //given
        Long postId = 12345L;

        given(postRepository.findByIdAndDeletedAtIsNull(postId)).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> postService.getDetail(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
    }

    @Test
    void 내_게시글_조회가_성공한다() {
        //given
        Long userId = 1L;
        User user = User.create("test@test.com", "password123", "작성자닉네임");
        Post post = Post.builder()
                .title("상세 조회 제목")
                .content("상세 조회 내용")
                .user(user)
                .build();

        Image image = Image.builder()
                .path("test-image-url.jpg")
                .post(post)
                .build();

        given(postRepository.findAllByUserIdAndDeletedAtIsNull(userId)).willReturn(List.of(post));
        given(imageRepository.findByPost(post)).willReturn(image);

        // when
        List<PostGetMyResponseDto> result = postService.getMy(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNickname()).isEqualTo("작성자닉네임");
    }
}