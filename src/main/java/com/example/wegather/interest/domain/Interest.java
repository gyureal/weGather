package com.example.wegather.interest.domain;

import com.example.wegather.global.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Interest extends BaseTimeEntity {
  @Id
  @GeneratedValue
  private Long id;
  @Column(unique = true)
  private String name;

  private Interest(String name) {
    this.name = name;
  }

  public static Interest of(String name) {
    return new Interest(name);
  }
}
