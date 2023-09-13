package com.example.wegather.auth.dto;

import lombok.Getter;

@Getter
public class SignInRequest {
  private String username;
  private String password;

  private SignInRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public static SignInRequest of(String username, String password) {
    return new SignInRequest(username, password);
  }
}
