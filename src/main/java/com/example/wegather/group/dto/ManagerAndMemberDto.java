package com.example.wegather.group.dto;

import com.example.wegather.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class ManagerAndMemberDto {
  private String name;
  private String introduction;
  private String image;
  private boolean isAdmin;

  public static ManagerAndMemberDto from(Member member, boolean isAdmin) {
    return ManagerAndMemberDto.builder()
        .name(member.getUsername())
        .introduction(member.getIntroductionText())
        .image(member.getProfileImage())
        .isAdmin(isAdmin)
        .build();
  }
}
