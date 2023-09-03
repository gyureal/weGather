package com.example.wegather.config.exceptionHandle;

import com.example.wegather.global.exception.ErrorCode;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.exception.customException.NoPermissionException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String INFO_LOG_TEMPLATE = "## info : {}, {}";
    private static final String ERROR_LOG_TEMPLATE = "## error : {}, {}";

    /**
     * 예외에 대한 로그 ID를 생성합니다.
     * UUID 로 랜덤값을 생성한 후 로그를 남깁니다.
     * @return UUID
     */
    private UUID generateLogId() {
        return UUID.randomUUID();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        UUID uuid = generateLogId();
        log.info(INFO_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalStateException ex) {
        UUID uuid = generateLogId();
        log.info(INFO_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, ex);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        UUID uuid = generateLogId();
        log.info(INFO_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        UUID uuid = generateLogId();
        log.info(INFO_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, ex);
    }

    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNoPermissionException(NoPermissionException ex) {
        UUID uuid = generateLogId();
        log.info(INFO_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, ex);
    }

    /**
     * 상위에 정의되지 않은 나머지 에러를 처리합니다.
     * - 500 서버 에러로 반환합니다.
     * - 로그 레벨을 error 로 남깁니다.
     * - 자세한 에러 메세지는 로그로만 남기고, 외부 반환시에는 숨깁니다.
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        UUID uuid = generateLogId();
        log.error(ERROR_LOG_TEMPLATE, uuid, ex.getClass().getSimpleName(), ex);
        return ErrorResponse.of(uuid, new Exception(ErrorCode.INTERNAL_SERVER_ERROR.getDescription()));
    }
}
