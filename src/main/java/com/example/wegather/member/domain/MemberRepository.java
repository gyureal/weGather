package com.example.wegather.member.domain;

import com.example.wegather.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByUsername(String username);

  Optional<Member> findByUsername(String username);

  @EntityGraph(attributePaths = {"memberAlarmSetting"})
  Optional<Member> findWithAlarmSettingByUsername(String username);

  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  @EntityGraph(attributePaths = {"memberInterests", "memberAlarmSetting"})
  Optional<Member> findWithInterestsAndAlarmById(Long memberId);
}
