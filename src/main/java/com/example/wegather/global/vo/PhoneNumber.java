package com.example.wegather.global.vo;

import lombok.Getter;

@Getter
public class PhoneNumber {
  private final String value;

  private PhoneNumber(String value) {
    this.value = value;
  }

  public PhoneNumber of(String value) {
    validate(value);
    return new PhoneNumber(value);
  }

  void validate(String value) {

  }
}
