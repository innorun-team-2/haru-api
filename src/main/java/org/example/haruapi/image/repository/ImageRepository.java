package org.example.haruapi.image.repository;

import org.example.haruapi.image.entity.Image;
import org.example.haruapi.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    // 수정 전 => 게시글 1개당 이미지 조회 하는 쿼리
    Image findByPost(Post post);

    // 수정 후 => 게시글id 리스트를 in절로 넘겨서 해당하는 이미지를 한 번에 조회
    List<Image> findByPostIdIn(List<Long> postIds);

}
