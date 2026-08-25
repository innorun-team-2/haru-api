package org.example.haruapi.post.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ServiceException {

    public PostNotFoundException(String message) {
        super(
                HttpStatus.NOT_FOUND,
                "POST_NOT_FOUND",
                message
        );
    }
}
