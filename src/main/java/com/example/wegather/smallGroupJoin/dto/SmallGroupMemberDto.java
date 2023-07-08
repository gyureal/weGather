package com.example.wegather.smallGroupJoin.dto;

import com.example.wegather.member.dto.MemberDto;
import com.example.wegather.smallGroupJoin.domin.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.SmallGroupMember;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SmallGroupMemberDto {
  private final Long id;
  private final Long smallGroupId;
  private final MemberDto memberDto;
  private final MemberStatus status;
  private final LocalDateTime registerDatetime;

  public static SmallGroupMemberDto from(SmallGroupMember groupMember) {
    return SmallGroupMemberDto
        .builder()
        .id(groupMember.getId())
        .smallGroupId(groupMember.getId())
        .memberDto(MemberDto.from(groupMember.getMember()))
        .status(groupMember.getStatus())
        .registerDatetime(groupMember.getRegisteredDatetime())
        .build();
  }
}
