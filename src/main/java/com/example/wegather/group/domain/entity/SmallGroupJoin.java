package com.example.wegather.group.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.group.domain.vo.JoinRequestStatus;
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

  private SmallGroupJoin(SmallGroup smallGroup, Member member, JoinRequestStatus status) {
    this.smallGroup = smallGroup;
    this.member = member;
    this.status = status;
  }

  /**
   * 소모임 가입 객체를 만듭니다.
   * Request 상태로 만들어집니다.
   * @param smallGroup
   * @param member
   * @return
   */
  public static SmallGroupJoin of(SmallGroup smallGroup, Member member) {
    return new SmallGroupJoin(smallGroup, member, JoinRequestStatus.REQUEST);
  }

  /**
   * 선착순 상태로 소모임 가입 객체를 만듭니다.
   * @param smallGroup
   * @param member
   * @return
   */
  public static SmallGroupJoin ofAsFCFS(SmallGroup smallGroup, Member member) {
    return new SmallGroupJoin(smallGroup, member, JoinRequestStatus.FCFS);
  }

  public void approve() {
    status = JoinRequestStatus.APPROVE;
  }

  public void reject() {
    status = JoinRequestStatus.REJECT;
  }
}
