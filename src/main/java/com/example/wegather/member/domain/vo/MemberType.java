package com.example.wegather.member.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
  USER("유저"), ADMIN("관리자");
  private final String describe;
}
