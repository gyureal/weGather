package com.example.wegather.interest.dto;

import com.example.wegather.group.domain.entity.SmallGroupInterest;
import com.example.wegather.interest.domain.Interest;
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

  public static InterestDto from(SmallGroupInterest smallGroupInterest) {
    return InterestDto.builder()
        .id(smallGroupInterest.getInterest().getId())
        .name(smallGroupInterest.getInterest().getName())
        .build();
  }
}
