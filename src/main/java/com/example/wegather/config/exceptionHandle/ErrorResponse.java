package com.example.wegather.config.exceptionHandle;

import com.example.wegather.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
    private final String errorCode;
    private final String description;
    private final LocalDateTime time;
    private final UUID logId;

    /**
     * 예외 반환 객체를 생성합니다.
     * Exception 의 Message 에 해당하는 ErrorCode 가 있는지 확인합니다.
     *    - ErrorCode 가 있을 경우 ErrorCode 를 사용하여 반환객체를 생성합니다.
     *    - ErrorCode 가 없는 경우 Exception Message 를 사용하여 반환객체를 생성합니다.
     * @param logId 로그 ID
     * @param ex 발생한 Exception
     * @return 예외반환객체
     */
    public static ErrorResponse of(UUID logId, Exception ex) {
        if (ErrorCode.isErrorCodeExists(ex.getMessage())) {
            return ofErrorCode(logId, ex);
        }
        return ofExceptionMessage(logId, ex);
    }

    /**
     * errorCode 클래스로 부터 ErrorResponse 를 생성합니다.
     * @param logId
     * @param ex
     * @return
     */
    private static ErrorResponse ofErrorCode(UUID logId, Exception ex) {
        ErrorCode errorcode = ErrorCode.findCode(ex.getMessage());

        return ErrorResponse.builder()
            .errorCode(errorcode.getCode())
            .description(errorcode.getDescription())
            .time(LocalDateTime.now())
            .logId(logId)
            .build();
    }

    /**
     * exception message 로 부터 ErrorResponse 를 생성합니다.
     * @param logId
     * @param ex
     * @return
     */
    private static ErrorResponse ofExceptionMessage(UUID logId, Exception ex) {
        return ErrorResponse.builder()
            .errorCode("")
            .description(ex.getMessage())
            .time(LocalDateTime.now())
            .logId(logId)
            .build();
    }
}
