package org.example.haruapi.post.dto;

import lombok.Getter;
import org.example.haruapi.post.entity.Post;

@Getter
public class PostUpdateResponseDto {

    private final String title;
    private final String content;

    private PostUpdateResponseDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static PostUpdateResponseDto from(Post post) {
        return new PostUpdateResponseDto(
                post.getTitle(),
                post.getContent()
        );
    }

}
