package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SmallGroupJoinRepository extends JpaRepository<SmallGroupJoin, Long> {
  boolean existsBySmallGroupAndMember(SmallGroup smallGroup, Member member);

  @Query(
      value = "select distinct s "
      + " from SmallGroupJoin s"
      + "   inner join fetch s.member m"
      + " where s.smallGroup = :smallGroup"
      + "   and s.status = 'REQUEST'",
      countQuery = "select count(s) "
          + " from SmallGroupJoin s"
          + "   inner join s.member m"
          + " where s.smallGroup = :smallGroup"
          + "   and s.status = 'REQUEST'")
  Page<SmallGroupJoin> findRequestBySmallGroup(SmallGroup smallGroup, Pageable pageable);
}
