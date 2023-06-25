package com.example.wegather.global.vo;

import static com.example.wegather.global.Message.Error.STREET_ADDRESS_MUST_NOT_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {
  @Test
  @DisplayName("주소 생성에 성공합니다.")
  void createAddressSuccessfully() {
    String streetAddress = "서울특별시 남구 백송대로 401번길 9";
    Double longitude = 35.97664845766847;
    Double latitude = 126.99597295767953;

    Address result = Address.of(streetAddress, longitude, latitude);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("도로명주소가 비어있어서 주소 생성에 실패합니다.")
  void createAddressFailBecauseOfEmpty() {
    // given
    String streetAddress = "";
    Double longitude = 35.97664845766847;
    Double latitude = 126.99597295767953;

    // when // then
    assertThatThrownBy(() -> {
      Address result = Address.of(streetAddress, longitude, latitude);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(STREET_ADDRESS_MUST_NOT_EMPTY);
  }
}
