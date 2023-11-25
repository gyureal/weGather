package com.example.wegather.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateGroupDescriptionRequest {
  private String shortDescription;
  private String fullDescription;
  private String image;
  private String originalImageName;
}
