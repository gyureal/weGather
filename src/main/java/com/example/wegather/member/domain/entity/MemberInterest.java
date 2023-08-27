package com.example.wegather.member.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.dto.InterestDto;
import java.util.Objects;
import javax.persistence.Entity;
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
public class MemberInterest extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Member member;
  @ManyToOne
  private Interest interest;

  public MemberInterest(Member member, Interest interest) {
    this.member = member;
    this.interest = interest;
  }

  public static MemberInterest of(Member member, Interest interest) {
    return new MemberInterest(member, interest);
  }

  public InterestDto getInterestDto() {
    return InterestDto.from(interest);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemberInterest that = (MemberInterest) o;
    return Objects.equals(member, that.member) && Objects.equals(interest,
        that.interest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(member, interest);
  }
}
