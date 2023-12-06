package com.example.wegather.group.domain.repotitory;


import static org.assertj.core.api.Assertions.*;

import com.example.wegather.RepositoryTest;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupMemberRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.domain.entity.SmallGroupMember;
import com.example.wegather.group.domain.vo.SmallGroupMemberType;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

class SmallGroupMemberRepositoryTest extends RepositoryTest {
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  SmallGroupRepository smallGroupRepository;
  @Autowired
  SmallGroupMemberRepository smallGroupMemberRepository;
  @Autowired
  TestEntityManager em;

  Member member01;
  Member member02;
  SmallGroup smallGroup01;

  @BeforeEach
  void initData() {
    member01 = inserMember("member01");
    member02 = inserMember("member02");
    smallGroup01 = insertSmallGroup("/first", "firstGroup");

    insertSmallGroupMember(smallGroup01, member01, SmallGroupMemberType.GENERAL);
    insertSmallGroupMember(smallGroup01, member02, SmallGroupMemberType.MANAGER);
  }

  @DisplayName("소모임의_소모임_회원을_MemberType에_따라_정렬하여_조회합니다")
  @Test
  void findBySmallGroupOrderbyType_success() {
    List<SmallGroupMember> groupMembers = smallGroupMemberRepository.findBySmallGroupOrderbyType(
        smallGroup01);

    assertThat(groupMembers).hasSize(2);
    assertThat(groupMembers.get(0).getMember()).isEqualTo(member02);
    assertThat(groupMembers.get(1).getMember()).isEqualTo(member01);
  }

  private SmallGroupMember insertSmallGroupMember(SmallGroup smallGroup, Member member, SmallGroupMemberType type) {
    SmallGroupMember smallGroupMember = SmallGroupMember.of(smallGroup, member);
    if (type == SmallGroupMemberType.MANAGER) {
      smallGroupMember.changeTypeManager();
    }
    return em.persistAndFlush(smallGroupMember);
  }

  private Member inserMember(String username) {
    return em.persistAndFlush(Member.builder()
        .username(username)
        .build());
  }

  private SmallGroup insertSmallGroup(String path, String name) {
    return em.persistAndFlush(SmallGroup.builder()
            .path(path)
            .name(name)
        .build());
  }
}
