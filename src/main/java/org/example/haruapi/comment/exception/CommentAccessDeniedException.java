package org.example.haruapi.comment.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class CommentAccessDeniedException extends ServiceException {
    public CommentAccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, "COMMENT_ACCESS_DENIED", message);
    }
}
