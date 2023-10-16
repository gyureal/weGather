package com.example.wegather.auth.dto;

import com.example.wegather.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class MemberInfo {
  private Long id;
  private String username;
  private String email;
  private boolean isEmailVerified;

  public static MemberInfo from(Member member) {
    return MemberInfo.builder()
        .id(member.getId())
        .username(member.getUsername())
        .email(member.getEmail())
        .isEmailVerified(member.isEmailVerified())
        .build();
  }
}
