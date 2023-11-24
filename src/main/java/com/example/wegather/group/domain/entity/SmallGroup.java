package com.example.wegather.group.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.groupJoin.domain.entity.SmallGroupMember;
import com.example.wegather.groupJoin.domain.vo.SmallGroupMemberType;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.member.domain.entity.Member;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
  private String path;
  @ManyToOne(fetch = FetchType.LAZY)
  private Member leader;

  @OneToMany(mappedBy = "smallGroup")
  private Set<SmallGroupMember> members = new HashSet<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "smallGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SmallGroupInterest> smallGroupInterests = new HashSet<>();

  private String name;
  private String shortDescription;
  @Lob
  @Basic(fetch = FetchType.EAGER)
  private String fullDescription;
  private String image;
  private String banner;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "street_address")),
      @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
      @AttributeOverride(name = "latitude", column = @Column(name = "latitude"))
  })
  private Address address;
  private Long maxMemberCount;
  private LocalDateTime recruitingUpdatedDateTime;
  private LocalDateTime publishedDateTime;
  private LocalDateTime closedDateTime;
  private boolean recruiting = false;
  private boolean published = false;
  private boolean closed = false;
  private boolean useBanner = false;

  @Builder
  public SmallGroup(String path, String name, String shortDescription, String fullDescription ,Member leader, Address address,
      Long maxMemberCount) {
    this.path = path;
    this.name = name;
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
    this.leader = leader;
    this.address = address;
    this.maxMemberCount = maxMemberCount;
  }

  public void updateSmallGroupDescription(String shortDescription, String fullDescription) {
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
  }

  public boolean isLeader(Long id) {
    return Objects.equals(leader.getId(), id);
  }

  public void addInterest(Interest interest) {
    this.smallGroupInterests.add(SmallGroupInterest.of(this, interest));
  }

  public void removeInterest(Interest interest) {
    this.smallGroupInterests.remove(SmallGroupInterest.of(this, interest));
  }

  public boolean isExceedMaxMember(Long nowCount) {
    return maxMemberCount <= nowCount;
  }

  /**
   * 회원 ID가 해당 소모임의 회원에 포합되어 있는지 반환합니다.
   * @param memberId 회원 ID
   * @return
   */
  public boolean isMemberOrManager(Long memberId) {
    return containsInMember(memberId);
  }

  /**
   * 회원 ID가 해당 소모임의 관리자인지 판단합니다.
   * @param memberId 회원 ID
   * @return
   */
  public boolean isManager(Long memberId) {
    return members.stream()
        .filter(member -> member.getSmallGroupMemberType().equals(SmallGroupMemberType.MANAGER))
        .map(SmallGroupMember::getMemberId)
        .anyMatch(member -> member.equals(memberId));
  }

  /**
   * 소모임이 가입 가능한지 반환합니다.
   * @param isMember 해당 소모임의 회원인지 (isMember() 호출 결과)
   * @return
   */
  public boolean isJoinable(boolean isMember) {
    return isPublished() && isRecruiting() && !isMember;
  }

  public void updateBanner(String banner) {
    this.banner = banner;
  }

  /**
   * 배너 사용 여부를 변경합니다.
   * true <-> false
   */
  public void toggleUseBanner() {
    useBanner = !useBanner;
  }

  private boolean containsInMember(Long memberId) {
    return members.stream().map(SmallGroupMember::getMemberId)
        .anyMatch(memberId::equals);
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
