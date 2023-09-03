package com.example.wegather.groupJoin.domain.repository;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.groupJoin.domain.entity.SmallGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupMemberRepository extends JpaRepository<SmallGroupMember, Long> {
  Long countBySmallGroup(SmallGroup smallGroup);
}
