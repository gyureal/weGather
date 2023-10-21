package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroup;
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
  private String path;
  @NotEmpty
  private String name;
  private String shortDescription;
  private String fullDescription;
}
