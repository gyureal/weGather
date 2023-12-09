package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroupJoin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GroupJoinRequestDto {
  private Long smallGroupJoinId;
  private Long memberId;
  private String username;
  private String email;
  private String introduction;
  private String profileImage;

  public static GroupJoinRequestDto from(SmallGroupJoin smallGroupJoin) {
    return GroupJoinRequestDto.builder()
        .smallGroupJoinId(smallGroupJoin.getId())
        .memberId(smallGroupJoin.getMember().getId())
        .username(smallGroupJoin.getMember().getUsername())
        .email(smallGroupJoin.getMember().getEmail())
        .introduction(smallGroupJoin.getMember().getIntroductionText())
        .profileImage(smallGroupJoin.getMember().getProfileImage())
        .build();
  }
}
