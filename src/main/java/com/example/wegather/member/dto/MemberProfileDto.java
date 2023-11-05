package com.example.wegather.member.dto;

import com.example.wegather.member.domain.entity.Member;
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
  private String profileImage;
  private boolean groupCreatedByEmail;
  private boolean groupCreatedByWeb;
  private boolean joinResultByEmail;
  private boolean joinResultByWeb;
  private boolean groupActivityByEmail;
  private boolean groupActivityByWeb;

  public static MemberProfileDto from(Member member) {
    return MemberProfileDto.builder()
        .username(member.getUsername())
        .email(member.getEmail())
        .joinedAt(member.getJoinedAt())
        .emailVerified(member.isEmailVerified())
        .introductionText(member.getIntroductionText())
        .profileImage(member.getProfileImage())
        .groupCreatedByEmail(member.getMemberAlarmSetting().isGroupCreatedByEmail())
        .groupCreatedByWeb(member.getMemberAlarmSetting().isGroupCreatedByWeb())
        .joinResultByEmail(member.getMemberAlarmSetting().isJoinResultByEmail())
        .joinResultByWeb(member.getMemberAlarmSetting().isJoinResultByWeb())
        .groupActivityByEmail(member.getMemberAlarmSetting().isGroupActivityByEmail())
        .groupActivityByWeb(member.getMemberAlarmSetting().isGroupActivityByWeb())
        .build();
  }
}
