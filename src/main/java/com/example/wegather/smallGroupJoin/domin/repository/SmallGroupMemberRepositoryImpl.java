package com.example.wegather.smallGroupJoin.domin.repository;

import static com.example.wegather.group.domain.entity.QSmallGroup.smallGroup;
import static com.example.wegather.member.domain.entity.QMember.member;
import static com.example.wegather.smallGroupJoin.domin.entity.QSmallGroupMember.smallGroupMember;

import com.example.wegather.smallGroupJoin.domin.vo.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.entity.SmallGroupMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SmallGroupMemberRepositoryImpl implements SmallGroupMemberRepositoryQuerydsl{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<SmallGroupMember> search(Long id, @Nullable MemberStatus status, Pageable pageable) {
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

  private BooleanExpression statusEq(@Nullable MemberStatus status) {
    return status != null ? smallGroupMember.status.eq(status) : null;
  }
}
