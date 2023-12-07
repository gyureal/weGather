package com.example.wegather.group.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinRequestStatus {
  REQUEST("요청"),
  APPROVE("승인"),
  REJECT("거부"),
  FCFS("선착순");
  private final String description;
}
