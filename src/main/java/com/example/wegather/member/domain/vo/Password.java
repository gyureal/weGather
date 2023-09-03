package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.exception.ErrorCode.*;

import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {
  private static final Pattern PASSWORD_RULE = Pattern.compile("^[A-Za-z0-9]{4,12}$");
  private String value;

  private Password(String value, PasswordEncoder passwordEncoder) {
    validatePasswordRule(value);
    this.value = passwordEncoder.encode(value);
  }

  /**
   * 비밀번호를 생성합니다.
   * 영문 대소문자, 숫자로 이루어진 4자 - 12자 사이의 문자
   * @param value 비밀번호
   * @param passwordEncoder 비밀번호를 암호화 하기 위한 객체
   * @return
   * @throws IllegalArgumentException 비밀번호 규칙에 맞지 않을 경우 예외를 던집니다.
   */
  public static Password of(String value, PasswordEncoder passwordEncoder) {
    return new Password(value, passwordEncoder);
  }

  void validatePasswordRule(String value) {
    if (!PASSWORD_RULE.matcher(value).matches()) {
      throw new IllegalArgumentException(PASSWORD_RULE_VIOLATION.getDescription());
    }
  }
}
