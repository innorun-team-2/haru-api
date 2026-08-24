package org.example.haruapi.post.repository;

import org.example.haruapi.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 전체 조회
    List<Post> findAllByDeletedAtIsNull();

    // 게시글 상세 조회
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    // 내 게시글 조회
    List<Post> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
