package com.example.wegather.member.domain.vo;


import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interests {
  private String value;

  private Interests(String value) {
    this.value = value;
  }

  public static Interests of(String value) {
    return new Interests(value);
  }
}
