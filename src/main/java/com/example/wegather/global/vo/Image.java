package com.example.wegather.global.vo;


import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
  private String value;

  private Image(String value) {
    this.value = value;
  }

  public static Image of(String value) {
    return new Image(value);
  }
}
