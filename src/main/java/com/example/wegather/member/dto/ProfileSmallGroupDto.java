package com.example.wegather.member.dto;

import com.example.wegather.global.vo.SmallGroupStatus;
import com.example.wegather.group.domain.entity.SmallGroup;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileSmallGroupDto {
  private String path;
  private String name;  // 소모임 명
  private String image; // 소모임 이미지
  private LocalDateTime createdAt;
  private SmallGroupStatus status;

  public static ProfileSmallGroupDto from(SmallGroup smallGroup) {
    return ProfileSmallGroupDto.builder()
        .path(smallGroup.getPath())
        .name(smallGroup.getName())
        .image(smallGroup.getImage())
        .createdAt(smallGroup.getCreatedAt())
        .status(smallGroup.getStatus())
        .build();
  }
}
