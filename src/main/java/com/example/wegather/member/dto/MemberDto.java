package com.example.wegather.member.dto;

import com.example.wegather.global.dto.AddressDto;
import com.example.wegather.interest.dto.InterestDto;
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
  private String email;
  private String phoneNumber;
  private AddressDto address;
  private MemberType memberType;
  private String profileImage;
  @Builder.Default
  private List<InterestDto> interests = new ArrayList<>();

  public static MemberDto from(Member member) {
    return MemberDto.builder()
        .id(member.getId())
        .username(member.getUsername())
        .email(member.getEmail())
        .phoneNumber(member.getPhoneNumber().getValue())
        .address(AddressDto.from(member.getAddress()))
        .memberType(member.getMemberType())
        .profileImage(member.getProfileImage())
        .interests(member.getInterestDtos())
        .build();
  }
}
