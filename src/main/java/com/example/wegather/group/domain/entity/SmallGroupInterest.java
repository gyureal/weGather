package com.example.wegather.group.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.dto.InterestDto;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity @EqualsAndHashCode(of = {"smallGroup", "interest"})
public class SmallGroupInterest extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private SmallGroup smallGroup;
  @ManyToOne
  private Interest interest;

  public SmallGroupInterest(SmallGroup smallGroup, Interest interest) {
    this.smallGroup = smallGroup;
    this.interest = interest;
  }

  public static SmallGroupInterest of(SmallGroup smallGroup, Interest interest) {
    return new SmallGroupInterest(smallGroup, interest);
  }

  public InterestDto getInterestDto() {
    return InterestDto.from(interest);
  }
}
