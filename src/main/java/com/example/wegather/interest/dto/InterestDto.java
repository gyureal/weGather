package com.example.wegather.interest.dto;

import com.example.wegather.interest.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InterestDto {
  private Long id;
  private String name;

  public static InterestDto from(Interest interest) {
    return InterestDto.builder()
        .id(interest.getId())
        .name(interest.getName())
        .build();
  }
}
