package com.example.wegather.group.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GroupSearchCondition {
  private String groupName;
  private List<String> interests;
  private String streetAddress;
  private String leaderUsername;
  private Integer maxMemberCountFrom;
  private Integer maxMemberCountTo;
}
