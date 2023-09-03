package com.example.wegather.global.vo;

import static com.example.wegather.global.exception.ErrorCode.*;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
  private String streetAddress; // 도로명주소
  private Double longitude;   // 경도
  private Double latitude;   // 위도

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
      throw new IllegalArgumentException(STREET_ADDRESS_MUST_NOT_EMPTY.getDescription());
    }
  }
}
