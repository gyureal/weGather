package com.example.wegather.group.domain;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.vo.MaxMemberCount;
import com.example.wegather.interest.domain.Interests;
import com.example.wegather.interest.domain.InterestsConverter;
import com.example.wegather.member.domain.Member;
import java.util.List;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@Entity
@Table(name = "SMALL_GROUP")
public class SmallGroup extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String description;
  @ManyToOne
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

  //@Embedded
  //@AttributeOverride(name = "value", column = @Column(name = "interests"))
  @Convert(converter = InterestsConverter.class)
  private Interests interests;

  public void updateGroupTotalInfo(String name, String description, Address address, MaxMemberCount maxMemberCount) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.maxMemberCount = maxMemberCount;
  }

  public boolean isLeader(String username) {
    return leader.getUsername().getValue().equals(username);
  }

  public Set<String> getInterests() {
    return interests.getValue();
  }

  public void addInterest(String interest) {
    interests.add(interest);
  }

  public boolean removeInterest(String interest) {
    return interests.remove(interest);
  }

  public boolean isInterestsNull() {
    return interests == null;
  }

  public List<String> getInterestsToList() {
    return interests.convertToList();
  }
}
