package org.example.haruapi.user.exception;

import org.example.haruapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class DuplicateNicknameException extends ServiceException {

    private static final String CODE = "USER_NICKNAME_ALREADY_EXISTS";
    private static final String MESSAGE = "이미 사용 중인 닉네임입니다.";

    public DuplicateNicknameException() {
        super(HttpStatus.CONFLICT, CODE, MESSAGE);
    }
}
