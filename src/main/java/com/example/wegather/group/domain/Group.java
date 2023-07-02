package com.example.wegather.group.domain;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.vo.MaxMemberCount;
import com.example.wegather.member.domain.Member;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "SMALL_GROUP")
public class Group extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String description;
  @OneToOne
  private Member leader;
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "street_address")),
      @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
      @AttributeOverride(name = "latitude", column = @Column(name = "latitude"))
  })
  private Address address;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "max_member_count"))
  private MaxMemberCount maxMemberCount;

  public void updateGroupTotalInfo(String name, String description, Address address, MaxMemberCount maxMemberCount) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.maxMemberCount = maxMemberCount;
  }
}
