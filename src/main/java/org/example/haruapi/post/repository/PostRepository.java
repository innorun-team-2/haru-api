package org.example.haruapi.post.repository;

import org.example.haruapi.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 수정 전 => 게시글 전체 조회 (기존에 사용했던 쿼리)
    List<Post> findAllByDeletedAtIsNull();

    // 수정 후 => 게시글 전체 조회 (미리 User 정보까지 묶어서 조회) fetch join 적용, 페이징 처리
    @Query(value = "select p from Post p join fetch p.user where p.deletedAt is null",
            countQuery = "select count(p) from Post p where p.deletedAt is null")
    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);

    // 게시글 상세 조회
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    // 내 게시글 조회
    List<Post> findAllByUserIdAndDeletedAtIsNull(Long userId);
}


