package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupJoinRepository extends JpaRepository<SmallGroupJoin, Long> {
  boolean existsBySmallGroupAndMember(SmallGroup smallGroup, Member member);
}
