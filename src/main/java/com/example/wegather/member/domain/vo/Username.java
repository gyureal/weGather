package com.example.wegather.member.domain.vo;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class Username {

  private static final String USERNAME_MUST_GREATER_THAN_FIVE = "사용자 아이디는 5글자 이상이어야합니다.";
  private final String value;

  private Username(String value) {
    validateNamingRule(value);
    this.value = value;
  }

  public Username of(String value) {
    return new Username(value);
  }

  /**
   * username 네이밍 규칙 유효성 검사
   * 1. 값이 없으면 안됩니다.
   * 2. 5글자 이상이어야 합니다.
   * @throws IllegalArgumentException 사용자 아이디가 5글자 아싱아 아닐때 예외를 던집니다.
   * @param value
   */
  void validateNamingRule(String value) {
    if (!StringUtils.hasText(value) && value.length() > 5) {
      throw new IllegalArgumentException(USERNAME_MUST_GREATER_THAN_FIVE);
    }
  }
}
