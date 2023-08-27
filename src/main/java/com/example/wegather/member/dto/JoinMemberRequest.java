package com.example.wegather.member.dto;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.interest.domain.Interests;
import java.util.ArrayList;
import java.util.List;
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
  @NotNull
  private MemberType memberType;
}
