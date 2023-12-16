package com.example.wegather.member.domain;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.ProfileSmallGroupDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByUsername(String username);

  Optional<Member> findByUsername(String username);

  @EntityGraph(attributePaths = {"memberAlarmSetting"})
  Optional<Member> findWithAlarmSettingByUsername(String username);

  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  @EntityGraph(attributePaths = {"memberInterests", "memberInterests.interest", "memberAlarmSetting"})
  Optional<Member> findWithInterestsAndAlarmById(Long memberId);

  @Query("select sg "
      + "from SmallGroupMember sgm "
      +   "inner join sgm.smallGroup sg "
      +   "inner join sgm.member m "
      + "where sgm.member.id = :memberId "
      + "and sg.leader.id != :memberId")
  List<SmallGroup> findJoinSmallGroupsByMemberId(Long memberId);

  @Query("select sg "
      + "from SmallGroup sg "
      + "where sg.leader.id = :memberId")
  List<SmallGroup> findCreateSmallGroupsByMemberId(Long memberId);
}
