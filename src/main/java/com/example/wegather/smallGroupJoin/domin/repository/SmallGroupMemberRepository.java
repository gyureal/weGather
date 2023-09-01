package com.example.wegather.smallGroupJoin.domin.repository;

import com.example.wegather.smallGroupJoin.domin.entity.SmallGroupMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupMemberRepository extends JpaRepository<SmallGroupMember, Long>
    , SmallGroupMemberRepositoryQuerydsl{

  boolean existsBySmallGroup_IdAndMember_Id(Long smallGroupId, Long MemberId);

  Optional<SmallGroupMember> findBySmallGroup_IdAndMember_Id(Long smallGroupId, Long MemberId);
}
