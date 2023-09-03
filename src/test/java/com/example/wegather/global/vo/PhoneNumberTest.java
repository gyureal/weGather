package com.example.wegather.global.vo;

import static com.example.wegather.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberTest {
  @Test
  @DisplayName("전화번호 생성에 성공합니다.")
  void createPhoneNumberSuccessfully() {
    String phoneNumber = "010-1234-1234";

    PhoneNumber result = PhoneNumber.of(phoneNumber);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("전화번호 형식에 맞지 않아 생성에 실패합니다.")
  void createPhoneNumberFailBecauseOfLessThanFour() {
    // given
    String phoneNumber = "010-11111-42323";

    // when // then
    assertThatThrownBy(() -> {
      PhoneNumber result = PhoneNumber.of(phoneNumber);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(PHONE_NUMBER_RULE_VIOLATION.getDescription());
  }
}
