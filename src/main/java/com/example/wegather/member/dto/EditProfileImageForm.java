package com.example.wegather.member.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EditProfileImageForm {
  @NotEmpty
  private String image;
  @NotEmpty
  private String originalImageName;
}
