package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.vo.RecruitingProcess;
import com.example.wegather.group.domain.vo.SmallGroupStatus;
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
  private String recruitingProcess;
  private boolean published;
  private boolean closed;
  private boolean joinable;
  private boolean managerOrMember;
  private SmallGroupStatus status;
  private boolean joinRequested;

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
        .recruitingProcess(smallGroup.getRecruitingProcess()!=null ? smallGroup.getRecruitingProcess().name() : "")
        .published(smallGroup.isPublished())
        .status(smallGroup.getStatus())
        .closed(smallGroup.isClosed())
        .build();
  }

  public void changeJoinable(boolean joinable) {
    this.joinable = joinable;
  }
  public void changeMemberOrManager(boolean managerOrMember) { this.managerOrMember = managerOrMember; }
  public void changeJoinRequested(boolean joinRequested) {this.joinRequested = joinRequested;}
}
