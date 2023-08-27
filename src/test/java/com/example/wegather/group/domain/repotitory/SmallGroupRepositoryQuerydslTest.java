package com.example.wegather.group.domain.repotitory;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.RepositoryTest;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.group.domain.SmallGroup;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.domain.vo.MaxMemberCount;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


class SmallGroupRepositoryQuerydslTest extends RepositoryTest {
  @Autowired
  TestEntityManager em;

  @Autowired
  EntityManager entityManager;

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  SmallGroupRepositoryQuerydsl smallGroupRepositoryQuerydsl;


  @BeforeEach
  void initTest() {
    smallGroupRepositoryQuerydsl = new SmallGroupRepositoryImpl(new JPAQueryFactory(entityManager));

    Member member01 = insertMember("member01");
    Member member02 = insertMember("member02");

    insertGroup("탁사모", "test", "서울특별시 세진대로", member01, MaxMemberCount.of(100));
    insertGroup("사상구 클라이밍", "test", "부산광역시 백양대로", member02, MaxMemberCount.of(80));
    insertGroup("농구 최고", "test", "서울특별시 세진대로", member01, MaxMemberCount.of(10));
    insertGroup("탁사모 부산", "test", "부산광역시 진양대로", member02, MaxMemberCount.of(20));
    insertGroup("서울 토익 스터디", "test", "서울특별시 OO", member01, MaxMemberCount.of(30));
  }

  @Test
  @DisplayName("이름만으로 조회")
  void searchGroupOnlyName() {
    // given
    SmallGroupSearchCondition cond = SmallGroupSearchCondition.builder()
        .groupName("탁사모")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(smallGroups).hasSize(2);
    assertThat(smallGroups).extracting(SmallGroup::getName).contains("탁사모", "탁사모 부산");
  }

  @Test
  @DisplayName("관심사로 조회")
  void searchGroupOnlyInterests() {

  }

  @Test
  @DisplayName("도로명 주소로 조회")
  void searchGroupOnlyStreetAddress() {
    SmallGroupSearchCondition cond = SmallGroupSearchCondition.builder()
        .streetAddress("서울")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(smallGroups).hasSize(3);
    assertThat(smallGroups).extracting(SmallGroup::getName).contains("탁사모", "농구 최고", "서울 토익 스터디");
  }

  @Test
  @DisplayName("그룹장의 username 으로 검색")
  void searchGroupByLeaderUsername() {
    SmallGroupSearchCondition cond = SmallGroupSearchCondition.builder()
        .leaderUsername("member01")
        .build();
    int size = 10;
    int page = 0;
    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(smallGroups).hasSize(3);
    assertThat(smallGroups).extracting(SmallGroup::getName).contains("탁사모", "농구 최고", "서울 토익 스터디");
  }

  @Test
  @DisplayName("소그룹 인원수 제한 범위로 조회")
  void searchGroupOnlyMaxMemberCountRange() {
    SmallGroupSearchCondition cond = SmallGroupSearchCondition.builder()
        .maxMemberCountFrom(10)
        .maxMemberCountTo(30)
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(smallGroups).hasSize(2);
  }

  @Test
  @DisplayName("그룹이름과 도로명 주소로 조회")
  void searchGroupGroupNameAndStreetAddress() {
    SmallGroupSearchCondition cond = SmallGroupSearchCondition.builder()
        .groupName("토익")
        .streetAddress("서울")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(smallGroups).hasSize(1);
    assertThat(smallGroups).extracting(SmallGroup::getName).contains("서울 토익 스터디");
  }

  Member insertMember(String username) {
    return em.persistAndFlush(Member.builder()
            .username(Username.of(username))
            .password(Password.of("1234", passwordEncoder))
            .memberType(MemberType.ROLE_USER)
            .address(Address.of("테스트주소", 123.21, 123.12))
            .phoneNumber(PhoneNumber.of("010-2222-3333"))
        .build());
  }

  SmallGroup insertGroup(String groupName, String description, String streetAddress, Member leader, MaxMemberCount maxMemberCount) {
    return em.persistAndFlush(SmallGroup.builder()
            .name(groupName)
            .description(description)
            .address(Address.of(streetAddress, 123.12, 134.12))
            .leader(leader)
            .maxMemberCount(maxMemberCount)
        .build());
  }
}
