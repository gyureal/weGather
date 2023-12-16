package com.example.wegather.global.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SmallGroupStatus {
  BEFORE_OPEN("준비중"),
  PUBLISHED("공개"),
  RECRUITING("모집중"),
  CLOSED("종료");

  private final String description;
}
