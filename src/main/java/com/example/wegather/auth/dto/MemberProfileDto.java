package com.example.wegather.auth.dto;

import com.example.wegather.member.domain.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class MemberProfileDto {
  private String username;
  private String email;
  private LocalDateTime joinedAt;
  private Boolean emailVerified;
  private String introductionText;



  public static MemberProfileDto from(Member member) {
    return MemberProfileDto.builder()
        .username(member.getUsername())
        .email(member.getEmail())
        .joinedAt(member.getJoinedAt())
        .emailVerified(member.isEmailVerified())
        .introductionText(member.getIntroductionText())
        .build();
  }
}
