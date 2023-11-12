package com.example.wegather.group.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class UpdateBannerRequest {
  @NotEmpty
  private String image;
  @NotEmpty
  private String originalImageName;
}
