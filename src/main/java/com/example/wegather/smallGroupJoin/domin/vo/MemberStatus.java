package com.example.wegather.smallGroupJoin.domin.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
  REQUEST("REQUEST", "등록요청")
  , APPROVED("APPROVED", "승인")
  , CANCELED("CANCELED", "취소")
  , LEAVE("LEAVE", "탈퇴");

  private final String code;
  private final String name;
}
