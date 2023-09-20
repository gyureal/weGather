package com.example.wegather.auth.dto;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfo {
  private String username;
  private String email;
  private boolean isVerified;

  public static MemberInfo from(Member member) {
    return new MemberInfo(member.getUsername(), member.getEmail(), member.isEmailVerified());
  }
}
