package org.example.haruapi.auth.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ServiceException {

    private static final String CODE = "INVALID_CREDENTIALS";
    private static final String MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, CODE, MESSAGE);
    }
}
