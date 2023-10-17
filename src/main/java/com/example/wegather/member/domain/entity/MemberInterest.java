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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @EqualsAndHashCode(of = {"member", "interest"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(
    attributeNodes = {
        @NamedAttributeNode("member"),
        @NamedAttributeNode("interest")
    }
)
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

  public String getInterestName() {
    return interest.getName();
  }

  public InterestDto getInterestDto() {
    return InterestDto.from(interest);
  }
}
