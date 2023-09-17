package com.example.wegather.auth.dto;

import com.example.wegather.global.vo.MemberType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpRequest {
  @NotEmpty
  private String username;
  @NotEmpty
  private String password;
  @NotEmpty
  private String email;
  private String phoneNumber;
}
