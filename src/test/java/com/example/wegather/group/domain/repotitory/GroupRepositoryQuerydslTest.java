package com.example.wegather.group.domain.repotitory;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.config.TestConfig;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.group.domain.Group;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.example.wegather.group.vo.MaxMemberCount;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Sql("/truncate.sql")
@DataJpaTest
class GroupRepositoryQuerydslTest {
  @Autowired
  TestEntityManager em;

  @Autowired
  EntityManager entityManager;

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  GroupRepositoryQuerydsl groupRepositoryQuerydsl;


  @BeforeEach
  void initTest() {
    groupRepositoryQuerydsl = new GroupRepositoryImpl(new JPAQueryFactory(entityManager));

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
    GroupSearchCondition cond = GroupSearchCondition.builder()
        .groupName("탁사모")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<Group> groups = groupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(groups).hasSize(2);
    assertThat(groups).extracting(Group::getName).contains("탁사모", "탁사모 부산");
  }

  @Test
  @DisplayName("관심사로 조회")
  void searchGroupOnlyInterests() {

  }

  @Test
  @DisplayName("도로명 주소로 조회")
  void searchGroupOnlyStreetAddress() {
    GroupSearchCondition cond = GroupSearchCondition.builder()
        .streetAddress("서울")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<Group> groups = groupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(groups).hasSize(3);
    assertThat(groups).extracting(Group::getName).contains("탁사모", "농구 최고", "서울 토익 스터디");
  }

  @Test
  @DisplayName("소그룹 인원수 제한 범위로 조회")
  void searchGroupOnlyMaxMemberCountRange() {
    GroupSearchCondition cond = GroupSearchCondition.builder()
        .maxMemberCountFrom(10)
        .maxMemberCountTo(30)
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<Group> groups = groupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(groups).hasSize(2);
  }

  @Test
  @DisplayName("그룹이름과 도로명 주소로 조회")
  void searchGroupGroupNameAndStreetAddress() {
    GroupSearchCondition cond = GroupSearchCondition.builder()
        .groupName("토익")
        .streetAddress("서울")
        .build();

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<Group> groups = groupRepositoryQuerydsl.search(cond, pageRequest).getContent();

    assertThat(groups).hasSize(1);
    assertThat(groups).extracting(Group::getName).contains("서울 토익 스터디");
  }

  Member insertMember(String username) {
    return em.persistAndFlush(Member.builder()
            .username(Username.of(username))
            .password(Password.of("1234", passwordEncoder))
            .memberType(MemberType.ROLE_USER)
            .address(Address.of("테스트주소", 123.21, 123.12))
            .phoneNumber(PhoneNumber.of("010-2222-3333"))
            .profileImage(Image.of("default.jpg"))
        .build());
  }

  Group insertGroup(String groupName, String description, String streetAddress, Member leader, MaxMemberCount maxMemberCount) {
    return em.persistAndFlush(Group.builder()
            .name(groupName)
            .description(description)
            .address(Address.of(streetAddress, 123.12, 134.12))
            .leader(leader)
            .maxMemberCount(maxMemberCount)
        .build());
  }
}
