package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.member.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SmallGroupJoinRepository extends JpaRepository<SmallGroupJoin, Long> {
  boolean existsBySmallGroupAndMember(SmallGroup smallGroup, Member member);
  Optional<SmallGroupJoin> findBySmallGroup_IdAndMember_Id(Long smallGroupId, Long memberId);
  boolean existsBySmallGroupAndMember_Id(SmallGroup smallGroup, Long memberId);

  @Query(
      value = "select distinct s "
      + " from SmallGroupJoin s"
      + "   inner join fetch s.member m"
      + " where s.smallGroup = :smallGroup"
      + "   and s.status = 'REQUEST'")
  List<SmallGroupJoin> findRequestBySmallGroup(SmallGroup smallGroup);
}
