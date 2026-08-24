package org.example.haruapi.global.s3.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class S3Exception extends ServiceException {

    // S3 업로드 실패시
    public S3Exception(String message) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "S3_UPLOAD_ERROR",
                message
        );
    }

}
