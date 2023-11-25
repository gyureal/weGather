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
  private boolean useBanner;
  private String banner;
  private String image;
  private boolean recruiting;
  private boolean published;
  private boolean closed;
  private boolean joinable;
  private boolean managerOrMember;

  public static SmallGroupDto from(SmallGroup smallGroup) {
    return SmallGroupDto.builder()
        .id(smallGroup.getId())
        .path(smallGroup.getPath())
        .name(smallGroup.getName())
        .shortDescription(smallGroup.getShortDescription())
        .fullDescription(smallGroup.getFullDescription())
        .image(smallGroup.getImage())
        .leaderId(smallGroup.getLeader().getId())
        .leaderUsername(smallGroup.getLeader().getUsername())
        .maxMemberCount(smallGroup.getMaxMemberCount())
        .banner(smallGroup.getBanner())
        .useBanner(smallGroup.isUseBanner())
        .recruiting(smallGroup.isRecruiting())
        .published(smallGroup.isPublished())
        .closed(smallGroup.isClosed())
        .build();
  }

  public void changeJoinable(boolean joinable) {
    this.joinable = joinable;
  }
  public void changeMemberOrManager(boolean managerOrMember) { this.managerOrMember = managerOrMember; }
}
