package com.example.wegather.member.domain.vo;

import lombok.Getter;

@Getter
public class Password {
  private final String value;

  private Password(String value) {
    this.value = value;
  }

  public Password of(String value) {
    validate(value);
    return new Password(value);
  }

  void validate(String value) {

  }
}
