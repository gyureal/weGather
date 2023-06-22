package com.example.wegather.global.vo;


import lombok.Getter;

@Getter
public class Image {
  private final String value;

  private Image(String value) {
    this.value = value;
  }

  public static Image of(String value) {
    return new Image(value);
  }
}
