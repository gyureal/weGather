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
public class UpdateSmallGroupRequest {
  @NotEmpty
  private String groupName;
  @NotEmpty
  private String description;
  @NotEmpty
  private String streetAddress;
  private Double longitude;
  private Double latitude;
  @NotNull
  @Min(0)
  private Integer maxMemberCount;
}
