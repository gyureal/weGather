package com.example.wegather.smallGroupJoin.domin.repository;

import static com.example.wegather.group.domain.QSmallGroup.*;
import static com.example.wegather.member.domain.QMember.*;
import static com.example.wegather.smallGroupJoin.domin.QSmallGroupMember.*;

import com.example.wegather.smallGroupJoin.domin.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.SmallGroupMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SmallGroupMemberRepositoryImpl implements SmallGroupMemberRepositoryQuerydsl{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<SmallGroupMember> search(Long id, Optional<MemberStatus> status, Pageable pageable) {
    List<SmallGroupMember> content = jpaQueryFactory
        .selectFrom(smallGroupMember)
        .join(smallGroupMember.smallGroup, smallGroup).fetchJoin()
        .leftJoin(smallGroupMember.member, member).fetchJoin()
        .where(
            smallGroupMember.smallGroup.id.eq(id),
            statusEq(status)
        ).offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    return PageableExecutionUtils.getPage(content, pageable, content::size);
  }

  private BooleanExpression statusEq(Optional<MemberStatus> status) {
    return status.isPresent() ? smallGroupMember.status.eq(status.get()) : null;
  }
}
