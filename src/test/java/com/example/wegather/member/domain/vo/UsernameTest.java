package com.example.wegather.member.domain.vo;

import static com.example.wegather.global.Message.Error.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsernameTest {

  @Test
  @DisplayName("회원 아이디 생성에 성공합니다.")
  void createUsernameSuccessfully() {
    String username = "test1";

    Username result = Username.of(username);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("회원아이디가 4자 미만이어서 생성에 실패합니다.")
  void createUsernameFailBecauseOfLessThanFour() {
    // given
    String username = "tes";

    // when // then
    assertThatThrownBy(() -> {
      Username result = Username.of(username);
    }).isInstanceOf(IllegalArgumentException.class)
            .hasMessage(USERNAME_RULE_VIOLATION);
  }

  @Test
  @DisplayName("회원아이디가 12자 초과이어서 생성에 실패합니다.")
  void createUsernameFailBecauseOfGreaterThanTwelve() {
    // given
    String username = "testtesttest1";

    // when // then
    assertThatThrownBy(() -> {
      Username result = Username.of(username);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(USERNAME_RULE_VIOLATION);
  }

  @Test
  @DisplayName("회원아이디에 대문자가 포함되어 생성에 실패합니다.")
  void createUsernameFailBecauseOfUpperCase() {
    // given
    String username = "Test1";

    // when // then
    assertThatThrownBy(() -> {
      Username result = Username.of(username);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(USERNAME_RULE_VIOLATION);
  }

}
