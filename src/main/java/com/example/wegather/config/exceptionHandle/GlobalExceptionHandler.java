package com.example.wegather.config.exceptionHandle;

import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.exception.customException.NoPermissionException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "알 수 없는 에러가 발생했습니다.";

    /**
     * 예외에 대한 로그 ID를 생성합니다.
     * UUID 로 랜덤값을 생성한 후 로그를 남깁니다.
     * @param ex
     * @return
     */
    private UUID generateLogId(Exception ex) {
        UUID uuid = UUID.randomUUID();
        log.error("## error : {}, {}", uuid, ex.getClass().getSimpleName(), ex);
        return uuid;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalStateException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNoPermissionException(NoPermissionException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        return ErrorResponse.of(generateLogId(ex), new Exception(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
