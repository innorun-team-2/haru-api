package org.example.haruapi.post.repository;

import org.example.haruapi.post.entity.Post;
import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=update")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새로운 게시글을 DB에 성공적으로 저장한다.")
    void 게시글이_성공적으로_저장된다() {
        // given
        User user = User.create("test@Test.com", "Password123!", "nickName");
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .user(savedUser)
                .build();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("제목 테스트");
        assertThat(savedPost.getContent()).isEqualTo("내용 테스트");
    }
}