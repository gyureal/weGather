package com.example.wegather.group.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UpdateBannerRequest {
  @NotEmpty
  private String image;
  @NotEmpty
  private String originalImageName;
}
