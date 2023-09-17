package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroup;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SmallGroupDto {
  private Long id;
  private String groupName;
  private String description;
  private Long leaderId;
  private String leaderUsername;
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  private Long maxMemberCount;

  public static SmallGroupDto from(SmallGroup smallGroup) {
    return SmallGroupDto.builder()
        .id(smallGroup.getId())
        .groupName(smallGroup.getName())
        .description(smallGroup.getDescription())
        .leaderId(smallGroup.getLeader().getId())
        .leaderUsername(smallGroup.getLeader().getUsername())
        .streetAddress(smallGroup.getAddress().getStreetAddress())
        .longitude(smallGroup.getAddress().getLongitude())
        .latitude(smallGroup.getAddress().getLatitude())
        .maxMemberCount(smallGroup.getMaxMemberCount())
        .build();
  }
}
