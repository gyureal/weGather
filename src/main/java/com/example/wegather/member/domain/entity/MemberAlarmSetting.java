package com.example.wegather.member.domain.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor @Builder
@EqualsAndHashCode(of = {"id"})
@Entity
public class MemberAlarmSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne(fetch = FetchType.LAZY)
  private Member member;
  private boolean groupCreatedByEmail;
  private boolean groupCreatedByWeb;
  private boolean joinResultByEmail;
  private boolean joinResultByWeb;
  private boolean groupActivityByEmail;
  private boolean groupActivityByWeb;

  public MemberAlarmSetting() {
    this.groupCreatedByEmail = false;
    this.groupCreatedByWeb = true;
    this.joinResultByEmail = false;
    this.joinResultByWeb = true;
    this.groupActivityByEmail = false;
    this.groupActivityByWeb = true;
  }

  public void changeMember(Member member) {
    this.member = member;
  }

  public void changeSettings(MemberAlarmSetting changeSettings) {
    this.groupCreatedByEmail = changeSettings.isGroupCreatedByEmail();
    this.groupCreatedByWeb = changeSettings.isGroupCreatedByWeb();
    this.joinResultByEmail = changeSettings.isJoinResultByEmail();
    this.joinResultByWeb = changeSettings.isJoinResultByWeb();
    this.groupActivityByEmail = changeSettings.isGroupActivityByEmail();
    this.groupActivityByWeb = changeSettings.isGroupActivityByWeb();
  }
}
