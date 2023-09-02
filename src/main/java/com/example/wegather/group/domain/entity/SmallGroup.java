package com.example.wegather.group.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.vo.MaxMemberCount;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.member.domain.entity.Member;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
@Table(name = "SMALL_GROUP")
public class SmallGroup extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  @ManyToOne(fetch = FetchType.LAZY)
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

  @OneToMany(mappedBy = "smallGroup")
  private List<SmallGroupMember> members;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "smallGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SmallGroupInterest> smallGroupInterests = new HashSet<>();

  @Builder
  public SmallGroup(String name, String description, Member leader, Address address,
      MaxMemberCount maxMemberCount) {
    this.name = name;
    this.description = description;
    this.leader = leader;
    this.address = address;
    this.maxMemberCount = maxMemberCount;
  }

  public void updateSmallGroupInfo(String name, String description, Address address, MaxMemberCount maxMemberCount) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.maxMemberCount = maxMemberCount;
  }

  public boolean isLeader(Long id) {
    return Objects.equals(leader.getId(), id);
  }

  public void addInterest(Interest interest) {
    this.smallGroupInterests.add(SmallGroupInterest.of(this, interest));
  }

  public void removeInterest(Interest interest) {
    this.smallGroupInterests.add(SmallGroupInterest.of(this, interest));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmallGroup that = (SmallGroup) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
