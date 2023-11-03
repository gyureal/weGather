package com.example.wegather.groupJoin.domain.repository;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.groupJoin.domain.entity.SmallGroupMember;
import com.example.wegather.member.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SmallGroupMemberRepository extends JpaRepository<SmallGroupMember, Long> {
  Long countBySmallGroup(SmallGroup smallGroup);

  @Query("select distinct sgm "
      + "from SmallGroupMember sgm "
      + "join fetch sgm.member "
      + "where sgm.smallGroup = :smallGroup "
      + "order by sgm.smallGroupMemberType desc")
  List<SmallGroupMember> findBySmallGroupOrderbyType(SmallGroup smallGroup);

  Optional<SmallGroupMember> findBySmallGroup_IdAndMember_Id(Long smallGroupId, Long memberId);
}
