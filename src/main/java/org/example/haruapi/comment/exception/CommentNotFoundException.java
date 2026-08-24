package org.example.haruapi.comment.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ServiceException {
    public CommentNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", message);
    }
}
