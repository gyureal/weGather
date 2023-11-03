package com.example.wegather.groupJoin.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.groupJoin.domain.vo.MemberType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class SmallGroupMember extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private SmallGroup smallGroup;

  @ManyToOne(fetch = FetchType.LAZY)    // 외래키를 설정해 주지 않기 위해 JoinColumn을 명시해 주지 않는다.
  private Member member;

  @Enumerated(value = EnumType.STRING)
  private MemberType memberType;

  private SmallGroupMember(SmallGroup smallGroup, Member member) {
    this.smallGroup = smallGroup;
    this.member = member;
    this.memberType = MemberType.GENERAL;
  }
  
  public static SmallGroupMember of(SmallGroup smallGroup, Member member) {
    return new SmallGroupMember(smallGroup, member);
  }

  public Long getMemberId() {
    return member.getId();
  }

  public void changeTypeManager() {
    this.memberType = MemberType.MANAGER;
  }
}
