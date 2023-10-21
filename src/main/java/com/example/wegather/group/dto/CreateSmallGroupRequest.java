package com.example.wegather.group.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateSmallGroupRequest {
  @NotEmpty
  private String groupName;
  private String shortDescription;
  private String fullDescription;
  @NotEmpty
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  @NotNull
  @Min(0)
  private Long maxMemberCount;
}
