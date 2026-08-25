package org.example.haruapi.global.exception;

import org.example.haruapi.global.dto.ApiResponse;
import org.example.haruapi.global.service.SlackNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SlackNotificationService slackNotificationService;

    public GlobalExceptionHandler(
            SlackNotificationService slackNotificationService
    ) {
        this.slackNotificationService = slackNotificationService;
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(
            ServiceException ex
    ) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(
                        ApiResponse.error(
                                ex.getCode(),
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청값이 올바르지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_REQUEST", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ) {
        slackNotificationService.sendException(e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.error(
                                "INTERNAL_SERVER_ERROR",
                                "서버 내부 오류가 발생했습니다."
                        )
                );
    }
}