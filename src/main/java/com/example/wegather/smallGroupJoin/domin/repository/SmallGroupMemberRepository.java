package com.example.wegather.smallGroupJoin.domin.repository;

import com.example.wegather.smallGroupJoin.domin.SmallGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupMemberRepository extends JpaRepository<SmallGroupMember, Long>
    , SmallGroupMemberRepositoryQuerydsl{

}
