package com.example.wegather.smallGroupJoin.domin.repository;

import com.example.wegather.smallGroupJoin.domin.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.SmallGroupMember;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmallGroupMemberRepositoryQuerydsl {
  Page<SmallGroupMember> search(Long id, Optional<MemberStatus> status, Pageable pageable);
}
