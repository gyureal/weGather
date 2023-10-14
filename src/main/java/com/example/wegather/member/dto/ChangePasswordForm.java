package com.example.wegather.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ChangePasswordForm {
  private String username;
  private String originalPassword;
  private String newPassword;
  private String newPasswordCheck;
}
