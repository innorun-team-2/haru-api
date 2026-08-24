package org.example.haruapi.post.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ImageRequiredException extends ServiceException {

    public ImageRequiredException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                "Post_IMAGE_REQUIRED",
                message
        );
    }
}
