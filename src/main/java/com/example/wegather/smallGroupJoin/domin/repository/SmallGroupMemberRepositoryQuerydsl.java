package com.example.wegather.smallGroupJoin.domin.repository;

import com.example.wegather.smallGroupJoin.domin.vo.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.entity.SmallGroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface SmallGroupMemberRepositoryQuerydsl {
  Page<SmallGroupMember> search(Long id, @Nullable MemberStatus status, Pageable pageable);
}
