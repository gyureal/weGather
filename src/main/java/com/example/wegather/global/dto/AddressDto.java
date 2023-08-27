package com.example.wegather.global.dto;

import com.example.wegather.global.vo.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddressDto {
  private String streetAddress;
  private Double latitude;
  private Double longitude;

  public AddressDto(String streetAddress, Double latitude, Double longitude) {
    this.streetAddress = streetAddress;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public static AddressDto from(Address address) {
    if (address == null) {
      return new AddressDto("주소 없음", 0.0, 0.0);
    }
    return new AddressDto(address.getStreetAddress(), address.getLatitude(), address.getLongitude());
  }
}
