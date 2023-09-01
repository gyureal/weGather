package com.example.wegather.group.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SmallGroupSearchCondition {
  private String smallGroupName;
  private String streetAddress;
  private String leaderUsername;
  private Integer maxMemberCountFrom;
  private Integer maxMemberCountTo;
  private String interest;
}
