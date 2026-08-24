package org.example.haruapi.user.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ServiceException {

    private static final String CODE = "USER_EMAIL_ALREADY_EXISTS";
    private static final String MESSAGE = "이미 사용 중인 이메일입니다.";

    public DuplicateEmailException() {
        super(HttpStatus.CONFLICT, CODE, MESSAGE);
    }
}
