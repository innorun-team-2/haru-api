package org.example.haruapi.comment.repository;

import org.example.haruapi.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndDeletedAtIsNull(Long postId);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    // 수정 전 => 게시글 1개당 postId에 해당하는 삭제되지 않은 comment 총 개수 반환
    Long countByPostIdAndDeletedAtIsNull(Long postId);

    // 수정 후 => group by 사용하여 여러 게시글의 댓글 개수를 한 번의 쿼리로 가져온다
    // 반환 타입인 Object[]의 [0]에는 postId가, [1]에는 댓글 개수가 들어간다.
    @Query(
            "select c.post.id, count(c.id) " +
                    "from Comment c " +
                    "where c.post.id in :postIds and c.deletedAt is null " +
                    "group by c.post.id")
    List<Object[]> countByPostIdIn(@Param("postIds") List<Long> postIds);
}
