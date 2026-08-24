package org.example.haruapi.post.repository;

import org.example.haruapi.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
