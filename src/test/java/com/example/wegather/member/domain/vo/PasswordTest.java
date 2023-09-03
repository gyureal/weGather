package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordTest {

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  @Test
  @DisplayName("비밀번호 생성에 성공합니다.")
  void createPasswordSuccessfully() {
    String password = "test1";

    Password result = Password.of(password, passwordEncoder);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("비밀번호가 4자 미만이어서 생성에 실패합니다.")
  void createPasswordFailBecauseOfLessThanFour() {
    // given
    String password = "tes";

    // when // then
    assertThatThrownBy(() -> {
      Password result = Password.of(password, passwordEncoder);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(PASSWORD_RULE_VIOLATION.getDescription());
  }

  @Test
  @DisplayName("비밀번호가 12자 초과이어서 생성에 실패합니다.")
  void createPasswordFailBecauseOfGreaterThanTwelve() {
    // given
    String password = "testtesttest1";

    // when // then
    assertThatThrownBy(() -> {
      Password result = Password.of(password, passwordEncoder);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(PASSWORD_RULE_VIOLATION.getDescription());
  }
}
