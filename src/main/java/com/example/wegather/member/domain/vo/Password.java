package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.Message.Error.PASSWORD_RULE_VIOLATION;

import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class Password {
  private static final Pattern PASSWORD_RULE = Pattern.compile("^[A-Za-z0-9]{4,12}$");
  private final String value;

  private Password(String value) {
    validatePasswordRule(value);
    this.value = value;
  }

  /**
   * 비밀번호를 생성합니다.
   * 영문 대소문자, 숫자로 이루어진 4자 - 12자 사이의 문자
   * @throws IllegalArgumentException 비밀번호 규칙에 맞지 않을 경우 예외를 던집니다.
   * @param value
   * @return
   */
  public static Password of(String value) {
    return new Password(value);
  }

  void validatePasswordRule(String value) {
    if (!PASSWORD_RULE.matcher(value).matches()) {
      throw new IllegalArgumentException(PASSWORD_RULE_VIOLATION);
    }
  }
}
