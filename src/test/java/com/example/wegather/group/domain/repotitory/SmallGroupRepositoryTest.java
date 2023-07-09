package com.example.wegather.group.domain.repotitory;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.RepositoryTest;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.group.domain.SmallGroup;
import com.example.wegather.group.vo.MaxMemberCount;
import com.example.wegather.interest.domain.Interests;
import com.example.wegather.member.domain.Member;
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

public class SmallGroupRepositoryTest extends RepositoryTest {

  @Autowired
  SmallGroupRepository smallGroupRepository;

  @Autowired
  TestEntityManager em;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private Member member01;

  private SmallGroup smallGroup01;

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
        //.interests(Interests.of(interestsSet))
        .build());

    Set<String> groupInterests = new HashSet<>(Arrays.asList("축구", "농구"));
    smallGroup01 = em.persistAndFlush(SmallGroup.builder()
        .name("testGroup")
        .description("This is testGroup")
        .leader(member01)
        .address(Address.of("test", 1.1, 2.2))
        .maxMemberCount(MaxMemberCount.of(20))
        .interests(Interests.of(groupInterests))
        .build());
  }

  @Test
  @DisplayName("소모임 저장 - 관심사 속성과 같이 추가")
  void saveSmallGroupWithInterests() {
    Set<String> interestsSet = new HashSet<>(Arrays.asList("축구", "농구"));

    SmallGroup smallGroup = SmallGroup.builder()
        .name("testGroup")
        .description("This is testGroup")
        .leader(member01)
        .address(Address.of("test", 1.1, 2.2))
        .maxMemberCount(MaxMemberCount.of(20))
        .interests(Interests.of(interestsSet))
        .build();

    SmallGroup saveGroup = smallGroupRepository.save(smallGroup);

    assertThat(saveGroup.getInterests()).contains("축구", "농구");
  }

  @Test
  @DisplayName("소그룹의 관심사 추가")
  @Rollback(value = false)
  void addInterestSuccessfully() {
    // given
    String interest = "테니스";
    SmallGroup smallGroup = smallGroupRepository.findById(smallGroup01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    // when
    //smallGroup.updateGroupTotalInfo("11","11",Address.of("11",11.2,11.2), MaxMemberCount.of(1));
    smallGroup.addInterest(interest);
    smallGroupRepository.save(smallGroup);
    em.flush();

    // then
    SmallGroup findGroup = smallGroupRepository.findById(smallGroup01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    assertThat(findGroup.getInterests()).contains("축구", "농구", "테니스");
  }

  @Test
  @DisplayName("소그룹의 관심사 제거")
  void removeInterestSuccessfully() {
    // given
    String interest = "축구";
    SmallGroup smallGroup = smallGroupRepository.findById(smallGroup01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    // when
    boolean deleted = smallGroup.removeInterest(interest);
    em.flush();

    // then
    SmallGroup findGroup = smallGroupRepository.findById(smallGroup01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));

    assertThat(deleted).isTrue();
    assertThat(findGroup.getInterests()).doesNotContain("축구");
  }
}
