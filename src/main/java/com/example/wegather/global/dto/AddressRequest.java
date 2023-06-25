package com.example.wegather.global.dto;

import com.example.wegather.global.vo.Address;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddressRequest {
  @NotEmpty
  private String streetAddress;
  private Double longitude;
  private Double latitude;

  public Address convertAddressEntity() {
    return Address.of(streetAddress, longitude, latitude);
  }
}
