package com.example.wegather.member.domain.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.dto.InterestDto;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Getter @EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
public class Member extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String password;
  private String email;

  private boolean emailVerified;
  private String emailCheckToken;
  private LocalDateTime emailCheckTokenGeneratedAt;
  private LocalDateTime joinedAt;
  private String introductionText;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "street_address")),
      @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
      @AttributeOverride(name = "latitude", column = @Column(name = "latitude"))
  })
  private Address address;
  @Enumerated(EnumType.STRING)
  private MemberType memberType;
  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "profile_image"))
  private Image profileImage;
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<MemberInterest> memberInterests = new HashSet<>();

  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  MemberAlarmSetting memberAlarmSetting;

  @Builder
  public Member(String username, String password, String email, Address address, MemberType memberType) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.address = address;
    this.memberType = memberType;
  }

  public void generateEmailCheckToken() {
    this.emailCheckToken = UUID.randomUUID().toString();
    this.emailCheckTokenGeneratedAt = LocalDateTime.now();
  }

  public void changeProfileImage(String storeImagePath) {
    profileImage = Image.of(storeImagePath);
  }

  public void changeAddress(Address address) {
    this.address = address;
  }

  public void addInterest(Interest interest) {
    this.memberInterests.add(MemberInterest.of(this, interest));
  }

  public void removeInterest(Interest interest) {
    this.memberInterests.remove(MemberInterest.of(this, interest));
  }

  public List<InterestDto> getInterestDtos() {
    return memberInterests.stream()
        .map(MemberInterest::getInterestDto)
        .collect(Collectors.toList());
  }

  public List<String> getInterestsName() {
    return memberInterests.stream()
        .map(MemberInterest::getInterestName)
        .collect(Collectors.toList());
  }

  public String getProfileImage() {
    if (profileImage == null) {
      return "";
    }
    return profileImage.getValue();
  }

  public boolean isValidToken(String token) {
    return emailCheckToken.equals(token);
  }

  public void completeSignUp() {
    emailVerified = true;
    joinedAt = LocalDateTime.now();
  }

  // 프로필 수정
  public void editProfile(String profile) {
    this.introductionText = profile;
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void changeMemberAlarmSetting(MemberAlarmSetting memberAlarmSetting) {
    if (this.memberAlarmSetting == null) {
      memberAlarmSetting.changeMember(this);
      this.memberAlarmSetting = memberAlarmSetting;
      return;
    }

    this.memberAlarmSetting.changeSettings(memberAlarmSetting);
  }
}

