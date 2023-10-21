package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SmallGroupDto {
  private Long id;
  private String path;
  private String name;
  private String shortDescription;
  private String fullDescription;
  private Long leaderId;
  private String leaderUsername;
  private Long maxMemberCount;

  public static SmallGroupDto from(SmallGroup smallGroup) {
    return SmallGroupDto.builder()
        .id(smallGroup.getId())
        .path(smallGroup.getPath())
        .name(smallGroup.getName())
        .shortDescription(smallGroup.getShortDescription())
        .fullDescription(smallGroup.getFullDescription())
        .leaderId(smallGroup.getLeader().getId())
        .leaderUsername(smallGroup.getLeader().getUsername())
        .maxMemberCount(smallGroup.getMaxMemberCount())
        .build();
  }
}
