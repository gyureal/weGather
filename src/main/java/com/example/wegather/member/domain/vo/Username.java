package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.Message.Error.USERNAME_RULE_VIOLATION;

import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {
  private static final Pattern USERNAME_RULE = Pattern.compile("^[a-z0-9]{4,12}$");
  private String value;

  private Username(String value) {
    validateNamingRule(value);
    this.value = value;
  }

  /**
   * username 을 생성합니다.버
   * 영문소문자, 숫자로 이루어진 4자 - 12자 사이의 글자
   * @param value
   * @throws IllegalArgumentException 회원 아이디 규칙에 맞지 않을 경우 예외를 던집니다.
   */
  public static Username of(String value) {
    return new Username(value);
  }

  void validateNamingRule(String value) {
    if (!USERNAME_RULE.matcher(value).matches()) {
      throw new IllegalArgumentException(USERNAME_RULE_VIOLATION);
    }
  }
}
