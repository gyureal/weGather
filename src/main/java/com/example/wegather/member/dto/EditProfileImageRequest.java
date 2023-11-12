package com.example.wegather.member.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class EditProfileImageRequest {
  @NotEmpty
  private String image;
  @NotEmpty
  private String originalImageName;
}
