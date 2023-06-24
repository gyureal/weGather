package com.example.wegather.member.dto;

import com.example.wegather.member.domain.vo.MemberType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JoinMemberRequest {
  @NotEmpty
  private String username;
  @NotEmpty
  private String password;
  @NotEmpty
  private String name;
  private String phoneNumber;
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  @NotNull
  private MemberType memberType;
}
