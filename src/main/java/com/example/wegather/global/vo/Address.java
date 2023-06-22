package com.example.wegather.global.vo;

import lombok.Getter;

@Getter
public class Address {
  private final String value;

  private Address(String value) {
    this.value = value;
  }

  public Address of(String value) {
    validate(value);
    return new Address(value);
  }

  void validate(String value) {

  }
}
