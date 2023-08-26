package com.example.wegather.member.dto;

import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.global.vo.MemberType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberDto {
  private Long id;
  private String username;
  private String name;
  private String phoneNumber;
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  private MemberType memberType;
  private String profileImage;
  @Builder.Default
  private List<String> interests = new ArrayList<>();

  public static MemberDto from(Member member) {
    return MemberDto.builder()
        .id(member.getId())
        .username(member.getUsername().getValue())
        .name(member.getName())
        .phoneNumber(member.getPhoneNumber().getValue())
        .streetAddress(member.getAddress().getStreetAddress())
        .longitude(member.getAddress().getLongitude())
        .latitude(member.getAddress().getLatitude())
        .memberType(member.getMemberType())
        .profileImage(member.getProfileImage().getValue())
        .interests(member.getInterestsToList())
        .build();
  }
}
