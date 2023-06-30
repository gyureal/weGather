package com.example.wegather.global.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
  ROLE_USER("유저"), ROLE_ADMIN("관리자");
  private final String describe;
}
