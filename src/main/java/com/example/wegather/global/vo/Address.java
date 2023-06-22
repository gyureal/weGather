package com.example.wegather.global.vo;

import static com.example.wegather.global.Message.Error.STREET_ADDRESS_MUST_NOT_EMPTY;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class Address {
  private final String streetAddress; // 도로명주소
  private final Double longitude;   // 경도
  private final Double latitude;   // 위도

  public Address(String streetAddress, Double longitude, Double latitude) {
    validateAddress(streetAddress, longitude, latitude);
    this.streetAddress = streetAddress;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public static Address of(String streetAddress, Double longitude, Double latitude) {
    return new Address(streetAddress, longitude, latitude);
  }

  void validateAddress(String streetAddress, Double longitude, Double latitude) {
    if (!StringUtils.hasText(streetAddress)) {
      throw new IllegalArgumentException(STREET_ADDRESS_MUST_NOT_EMPTY);
    }
  }
}
