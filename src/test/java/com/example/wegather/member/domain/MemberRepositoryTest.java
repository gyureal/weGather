package com.example.wegather.member.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wegather.RepositoryTest;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.interest.domain.Interests;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

class MemberRepositoryTest extends RepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  MemberRepository memberRepository;

  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private Member member01;

  @BeforeEach
  void init() {
    Set<String> interestsSet = new HashSet<>(Arrays.asList("탁구"));
    member01 = em.persistAndFlush(Member.builder()
        .username(Username.of("user01"))
        .password(Password.of("password01", passwordEncoder))
        .name("테스트유저")
        .phoneNumber(PhoneNumber.of("010-3333-2222"))
        .address(Address.of("test", 12.2, 1.2))
        .memberType(MemberType.ROLE_USER)
        .interests(Interests.of(interestsSet))
        .build());
  }

  @Test
  @DisplayName("회원 등록 - 관심사 등록")
  void insertMemberWithInterestsSuccessfully() {

    Set<String> interestsSet = new HashSet<>(Arrays.asList("배구", "야구"));
    Interests interests = Interests.of(interestsSet);

    Member member = memberRepository.save(Member.builder()
        .username(Username.of("user02"))
        .password(Password.of("password01", passwordEncoder))
        .name("테스트유저")
        .phoneNumber(PhoneNumber.of("010-3333-2222"))
        .address(Address.of("test", 12.2, 1.2))
        .memberType(MemberType.ROLE_USER)
        .interests(interests)
        .build());

    assertThat(member.getInterests()).contains("배구", "야구");
  }

  @Test
  @DisplayName("회원의 관심사 조회")
  void findMemberWithInterestsSuccessfully() {
    Set<String> interestsSet = new HashSet<>(Arrays.asList("배구", "야구"));
    Interests interests = Interests.of(interestsSet);
    Member saveMember = em.persistAndFlush(Member.builder()
        .username(Username.of("user01"))
        .password(Password.of("password01", passwordEncoder))
        .name("테스트유저")
        .phoneNumber(PhoneNumber.of("010-3333-2222"))
        .address(Address.of("test", 12.2, 1.2))
        .memberType(MemberType.ROLE_USER)
        .interests(interests)
        .build());

    Member member = memberRepository.findById(saveMember.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    assertThat(member.getInterests()).hasSize(2);
    assertThat(member.getInterests()).contains("배구", "야구");
  }

  @Test
  @DisplayName("회원의 관심사 추가")
  void addInterestSuccessfully() {
    // given
    String interest = "테니스";
    Member member = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    // when
    member.addInterest(interest);
    em.flush();

    // then
    Member findMember = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    assertThat(findMember.getInterests()).contains("테니스");
  }

  @Test
  @DisplayName("회원의 관심사 제거")
  void removeInterestSuccessfully() {
    // given
    String interest = "탁구";
    Member member = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    // when
    member.removeInterest(interest);
    em.flush();

    // then
    Member findMember = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    assertThat(findMember.getInterests()).doesNotContain("탁구");
  }
}
