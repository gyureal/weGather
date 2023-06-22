package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.Message.Error.PASSWORD_RULE_VIOLATION;
import static com.example.wegather.global.Message.Error.USERNAME_RULE_VIOLATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {
  @Test
  @DisplayName("비밀번호 생성에 성공합니다.")
  void createPasswordSuccessfully() {
    String password = "test1";

    Password result = Password.of(password);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("비밀번호가 4자 미만이어서 생성에 실패합니다.")
  void createPasswordFailBecauseOfLessThanFour() {
    // given
    String password = "tes";

    // when // then
    assertThatThrownBy(() -> {
      Password result = Password.of(password);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(PASSWORD_RULE_VIOLATION);
  }

  @Test
  @DisplayName("비밀번호가 12자 초과이어서 생성에 실패합니다.")
  void createPasswordFailBecauseOfGreaterThanTwelve() {
    // given
    String password = "testtesttest1";

    // when // then
    assertThatThrownBy(() -> {
      Password result = Password.of(password);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(PASSWORD_RULE_VIOLATION);
  }
}
