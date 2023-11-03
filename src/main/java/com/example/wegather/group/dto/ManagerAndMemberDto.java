package com.example.wegather.group.dto;

import com.example.wegather.groupJoin.domain.entity.SmallGroupMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class ManagerAndMemberDto {
  private String name;
  private String introduction;
  private String image;
  private String memberType;

  public static ManagerAndMemberDto from(SmallGroupMember groupMember) {
    return ManagerAndMemberDto.builder()
        .name(groupMember.getMember().getUsername())
        .introduction(groupMember.getMember().getIntroductionText())
        .image(groupMember.getMember().getProfileImage())
        .memberType(groupMember.getSmallGroupMemberType().name())
        .build();
  }
}
