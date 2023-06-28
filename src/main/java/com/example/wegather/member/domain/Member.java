package com.example.wegather.member.domain;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.member.domain.vo.Interests;
import com.example.wegather.member.domain.vo.MemberType;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class Member extends BaseTimeEntity {
  @Id
  @GeneratedValue
  private Long id;
  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "username"))
  private Username username;
  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "password"))
  private Password password;
  private String name;
  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "phone_number"))
  private PhoneNumber phoneNumber;
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
  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "interests"))
  Interests interests;

  public void changeProfileImage(String storeImagePath) {
    profileImage = Image.of(storeImagePath);
  }

  public void changeAddress(Address address) {
    this.address = address;
  }
}

