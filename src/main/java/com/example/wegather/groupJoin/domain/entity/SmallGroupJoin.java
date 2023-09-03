package com.example.wegather.groupJoin.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.groupJoin.domain.vo.JoinRequestStatus;
import com.example.wegather.member.domain.entity.Member;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SmallGroupJoin extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  private SmallGroup smallGroup;
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;
  @Enumerated(EnumType.STRING)
  private JoinRequestStatus status;

  private SmallGroupJoin(SmallGroup smallGroup, Member member) {
    this.smallGroup = smallGroup;
    this.member = member;
    this.status = JoinRequestStatus.REQUEST;
  }

  public static SmallGroupJoin of(SmallGroup smallGroup, Member member) {
    return new SmallGroupJoin(smallGroup, member);
  }

  public void approve() {
    status = JoinRequestStatus.APPROVE;
  }

  public void reject() {
    status = JoinRequestStatus.REJECT;
  }
}
