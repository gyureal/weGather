package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.member.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SmallGroupJoinRepository extends JpaRepository<SmallGroupJoin, Long> {
  boolean existsBySmallGroupAndMember(SmallGroup smallGroup, Member member);
  Optional<SmallGroupJoin> findBySmallGroup_IdAndMember_Id(Long smallGroupId, Long memberId);

  /**
   * 요청 상태의 데이터가 있는지 체크합니다.
   * @param smallGroupId 소모임 ID
   * @param memberId 회원 ID
   * @return
   */
  @Query(
          "select count(sgj.id) > 0 from SmallGroupJoin sgj "
          + "where sgj.smallGroup.id = :smallGroupId "
          + "  and sgj.member.id = :memberId"
          + "  and sgj.status = 'REQUEST'"
  )
  boolean existsRequestedJoin(Long smallGroupId, Long memberId);

  @Query(
      "select distinct s "
      + " from SmallGroupJoin s"
      + "   inner join fetch s.member m"
      + " where s.smallGroup = :smallGroup"
      + "   and s.status = 'REQUEST'")
  List<SmallGroupJoin> findRequestBySmallGroup(SmallGroup smallGroup);
}
