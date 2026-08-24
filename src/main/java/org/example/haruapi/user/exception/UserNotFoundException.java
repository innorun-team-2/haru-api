package org.example.haruapi.user.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ServiceException {

    private static final String CODE = "USER_NOT_FOUND";
    private static final String MESSAGE = "사용자를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, CODE, MESSAGE);
    }
}
