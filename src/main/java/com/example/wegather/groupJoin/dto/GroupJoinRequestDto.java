package com.example.wegather.groupJoin.dto;

import com.example.wegather.groupJoin.domain.entity.SmallGroupJoin;
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
  private String name;
  private String profileImage;

  public static GroupJoinRequestDto from(SmallGroupJoin smallGroupJoin) {
    return GroupJoinRequestDto.builder()
        .smallGroupJoinId(smallGroupJoin.getId())
        .memberId(smallGroupJoin.getMember().getId())
        .username(smallGroupJoin.getMember().getUsernameStr())
        .name(smallGroupJoin.getMember().getName())
        .profileImage(smallGroupJoin.getMember().getProfileImage())
        .build();
  }
}
