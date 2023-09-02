package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupMemberRepository extends JpaRepository<SmallGroupMember, Long> {
  Long countBySmallGroup(SmallGroup smallGroup);
}
