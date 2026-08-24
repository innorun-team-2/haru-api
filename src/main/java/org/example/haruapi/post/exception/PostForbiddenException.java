package org.example.haruapi.post.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class PostForbiddenException extends ServiceException {
    public PostForbiddenException(String message) {
        super(
                HttpStatus.FORBIDDEN,
                "POST_FORBIDDEN",
                message
        );
    }
}
