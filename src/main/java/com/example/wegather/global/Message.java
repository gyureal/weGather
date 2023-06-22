package com.example.wegather.global;

public class Message {
  public static class Error {
    public static final String INTEREST_NAME_ALREADY_EXISTS = "관심사 이름이 이미 존재합니다.";
    public static final String INTEREST_NOT_FOUND = "해당 관심사를 찾을 수 없습니다.";
    public static final String USERNAME_RULE_VIOLATION = "회원 아이디는 4-12자 사이의 영문 또는 숫자여야 합니다.";
    public static final String PASSWORD_RULE_VIOLATION = "비밀번호는 4-12자 사이의 영문 대소문자 또는 숫자여야합니다.";
    public static final String PHONE_NUMBER_RULE_VIOLATION = "올바른 전화번호를 입력해주세요.";
    public static final String STREET_ADDRESS_MUST_NOT_EMPTY = "도로명주소는 값이 비어있을 수 없습니다.";
  }
}
