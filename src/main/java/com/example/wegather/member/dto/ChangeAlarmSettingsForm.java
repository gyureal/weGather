package com.example.wegather.member.dto;

import com.example.wegather.member.domain.entity.MemberAlarmSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
public class ChangeAlarmSettingsForm {

  private boolean groupCreatedByEmail;
  private boolean groupCreatedByWeb;
  private boolean joinResultByEmail;
  private boolean joinResultByWeb;
  private boolean groupActivityByEmail;
  private boolean groupActivityByWeb;

  public MemberAlarmSetting toEntity() {
    return MemberAlarmSetting.builder()
        .groupCreatedByEmail(this.groupCreatedByEmail)
        .groupCreatedByWeb(this.groupCreatedByWeb)
        .joinResultByEmail(this.joinResultByEmail)
        .joinResultByWeb(this.joinResultByWeb)
        .groupActivityByEmail(this.groupActivityByEmail)
        .groupActivityByWeb(this.groupActivityByWeb)
        .build();
  }

}
