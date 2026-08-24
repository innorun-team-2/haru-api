package org.example.haruapi.image.repository;

import org.example.haruapi.image.entity.Image;
import org.example.haruapi.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByPost(Post post);
}
