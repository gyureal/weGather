package com.example.wegather.group.domain.repotitory;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.RepositoryTest;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.member.domain.entity.Member;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;


class SmallGroupRepositoryQuerydslTest extends RepositoryTest {
  @Autowired
  TestEntityManager em;

  @Autowired
  EntityManager entityManager;

  SmallGroupRepositoryQuerydsl smallGroupRepositoryQuerydsl;


  @BeforeEach
  void initTest() {
    smallGroupRepositoryQuerydsl = new SmallGroupRepositoryImpl(entityManager);

    Member member01 = insertMember("member01");
    Member member02 = insertMember("member02");

    insertGroup("탁사모", "test", member01, 100L);
    insertGroup("사상구 클라이밍", "test", member02, 80L);
    insertGroup("농구 최고", "test", member01, 10L);
    insertGroup("탁사모 부산", "test", member02, 20L);
    insertGroup("서울 토익 스터디", "test", member01, 30L);
  }

  @Test
  //@Disabled
  @DisplayName("이름만으로 조회")
  void searchGroupOnlyName() {
    // given
    String title = "탁사모";

    int size = 10;
    int page = 0;

    PageRequest pageRequest = PageRequest.of(page, size);

    // when
    List<SmallGroup> smallGroups = smallGroupRepositoryQuerydsl.search(title, pageRequest).getContent();

    assertThat(smallGroups).hasSize(2);
    assertThat(smallGroups).extracting(SmallGroup::getName).contains("탁사모", "탁사모 부산");
  }

  Member insertMember(String username) {
    return em.persistAndFlush(Member.builder()
            .username(username)
            .password("1234")
            .memberType(MemberType.ROLE_USER)
        .build());
  }

  SmallGroup insertGroup(String groupName, String description, Member leader, Long maxMemberCount) {
    return em.persistAndFlush(SmallGroup.builder()
            .name(groupName)
            .shortDescription(description)
            .leader(leader)
            .maxMemberCount(maxMemberCount)
        .build());
  }
}
