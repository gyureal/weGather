package com.example.wegather.group.dto;

import com.example.wegather.group.domain.entity.SmallGroup;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class SmallGroupSearchDto {
  private String path;
  private String name;
  private String shortDescription;
  private String image;
  private List<String> interests;
  private long maxMemberCount;
  private int currentMemberCount;
  private LocalDateTime createdAt;

  public static SmallGroupSearchDto from (SmallGroup smallGroup) {
    return SmallGroupSearchDto.builder()
        .path(smallGroup.getPath())
        .name(smallGroup.getName())
        .shortDescription(smallGroup.getShortDescription())
        .image(smallGroup.getImage())
        .interests(smallGroup.getInterests())
        .maxMemberCount(smallGroup.getMaxMemberCount())
        .currentMemberCount(smallGroup.getCurrentMemberCount())
        .createdAt(smallGroup.getCreatedAt())
        .build();
  }
}
