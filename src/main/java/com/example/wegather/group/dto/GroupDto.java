package com.example.wegather.group.dto;

import com.example.wegather.group.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GroupDto {
  private Long id;
  private String groupName;
  private String description;
  private Long leaderId;
  private String leaderUsername;
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  private Integer maxMemberCount;

  public static GroupDto from(Group group) {
    return GroupDto.builder()
        .id(group.getId())
        .groupName(group.getName())
        .description(group.getDescription())
        .leaderId(group.getLeader().getId())
        .leaderUsername(group.getLeader().getUsername().getValue())
        .streetAddress(group.getAddress().getStreetAddress())
        .longitude(group.getAddress().getLongitude())
        .latitude(group.getAddress().getLatitude())
        .maxMemberCount(group.getMaxMemberCount().getValue())
        .build();
  }
}
