package com.example.wegather.config.exceptionHandle;

import static com.example.wegather.global.exception.ErrorCode.INVALID_INPUT_ERROR;

import com.example.wegather.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
    private final String errorCode;
    private final String description;
    private final List<ErrorDetail> errorDetails;
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
     * validation 결과인 BindingResult를 받아서 errorDetails에 담아 반환합니다.
     * @param logId
     * @param bindingResult
     * @param ex
     * @return
     */
    public static ErrorResponse of(UUID logId, BindingResult bindingResult, Exception ex) {
        return ErrorResponse.builder()
            .errorCode(INVALID_INPUT_ERROR.getCode())
            .description(INVALID_INPUT_ERROR.getDescription())
            .errorDetails(ErrorDetail.from(bindingResult))
            .time(LocalDateTime.now())
            .logId(logId)
            .build();
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
            .errorDetails(new ArrayList<>())
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

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorDetail {

        private final String field;
        private final String reason;

        public static ErrorDetail from(FieldError fieldError) {
            return new ErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage());
        }

        public static List<ErrorDetail> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                .map(ErrorDetail::from)
                .collect(Collectors.toList());
        }
    }
}
