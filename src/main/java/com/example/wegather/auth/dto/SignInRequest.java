package com.example.wegather.auth.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SignInRequest {
  @NotEmpty
  private String usernameOrEmail;
  @NotEmpty
  private String password;

  private SignInRequest(String usernameOrEmail, String password) {
    this.usernameOrEmail = usernameOrEmail;
    this.password = password;
  }

  public static SignInRequest of(String usernameOrEmail, String password) {
    return new SignInRequest(usernameOrEmail, password);
  }
}
