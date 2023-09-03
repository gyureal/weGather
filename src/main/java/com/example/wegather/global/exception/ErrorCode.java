package com.example.wegather.global.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  /**
   * 공통 에러 메세지
   * 규칙 : 1000 ~ 19999
   */
  INTERNAL_SERVER_ERROR("1000", "서버 내부 에러입니다."),
  FAIL_TO_UPLOAD_FILE("1001", "파일 업로드에 실패했습니다."),
  FAIL_TO_GET_FILE("1002", "파일을 가져오는데 실패했습니다."),
  FAIL_TO_DELETE_FILE("1003", "파일 삭제에 실패했습니다."),
  STREET_ADDRESS_MUST_NOT_EMPTY("1004", "도로명주소는 값이 비어있을 수 없습니다."),
  PHONE_NUMBER_RULE_VIOLATION("1003", "올바른 전화번호를 입력해주세요."),

  INVALID_INPUT_ERROR("1009","올바르지 않은 입력입니다."),

  /**
   * 회원 파트 에러 메세지
   * 규칙 : 2000 ~ 29999
   */
  MEMBER_NOT_FOUND("2000", "회원을 찾지 못했습니다."),
  USERNAME_NOT_FOUND("2001", "사용자 정보를 찾을 수 없습니다."),
  USERNAME_RULE_VIOLATION("2002", "회원 아이디는 규칙에 위배됩니다."),
  PASSWORD_RULE_VIOLATION("2003", "회원 비밀번호 규칙에 위배됩니다."),
  USERNAME_DUPLICATED("2004", "중복된 회원 ID 입니다."),

  /**
   * 관심사 파트 에러 메세지
   * 규칙 : 3000 ~ 39999
   */
  INTEREST_NOT_FOUND("3000", "관심사를 찾지 못했습니다."),
  INTEREST_NAME_ALREADY_EXISTS("3001", "관심사 명이 이미 존재합니다."),

  /**
   * 소모임 파트 에러 메세지
   * 규칙 : 4000 ~ 49999
   */
  SMALL_GROUP_NOT_FOUND("4000", "소모임을 찾지 못했습니다."),
  DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP("4001", "소모임 정보를 수정할 권한이 없습니다."),
  DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP("4002", "소모임을 삭제할 권한이 없습니다."),
  ALREADY_REQUEST_JOIN_MEMBER("4003", "이미 가입 요청한 회원입니다."),
  LEADER_CANNOT_REQUEST_JOIN("4004", "소모임장은 가입 요청할 수 없습니다."),
  LEADER_ONLY("4005", "소모임장만 가능합니다."),
  EXCESS_MAX_MEMBER_COUNT("4006", "최대 회원수를 초과하였습니다."),
  SMALL_GROUP_JOIN_NOT_FOUND("4007", "소모임 가입 요청을 찾을 수 없습니다.");
  

  private final String code;
  private final String description;

  private static final Map<String, ErrorCode> ERROR_CODE_MAP = new HashMap<>();

  static {
    for (ErrorCode errorCode : ErrorCode.values()) {
      ERROR_CODE_MAP.put(errorCode.description, errorCode);
    }
  }

  /**
   * 에러 메세지에 해당하는 에러코드가 존재하는지 확인합니다.
   * @param description 에러메세지
   * @return
   */
  public static boolean isErrorCodeExists(String description) {
    return ERROR_CODE_MAP.containsKey(description);
  }

  /**
   * 에러 메제지에 해당하는 에러코드 Enum 을 반환합니다.
   * @param description 에러메세지
   * @return
   */
  public static ErrorCode findCode(String description) {
    return ERROR_CODE_MAP.get(description);
  }
}
