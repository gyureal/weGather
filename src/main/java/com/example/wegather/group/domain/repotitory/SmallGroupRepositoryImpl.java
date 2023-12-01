package com.example.wegather.group.domain.repotitory;

import static com.example.wegather.group.domain.entity.QSmallGroup.smallGroup;
import static com.example.wegather.group.domain.entity.QSmallGroupInterest.*;
import static com.example.wegather.interest.domain.QInterest.*;
import static com.example.wegather.member.domain.entity.QMember.member;

import com.example.wegather.group.domain.entity.SmallGroup;
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
public class SmallGroupRepositoryImpl implements SmallGroupRepositoryQuerydsl {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<SmallGroup> search(String title, Pageable pageable) {
    List<SmallGroup> content = queryFactory
        .selectFrom(smallGroup)
        .join(smallGroup.leader, member)
        .where(
            groupNameContains(title)
            )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return PageableExecutionUtils.getPage(content, pageable, content::size);
  }

  private BooleanExpression groupNameContains(String groupName) {
    return groupName != null ? smallGroup.name.contains(groupName) : null;
  }

  @Override
  public Optional<SmallGroup> findWithInterestByPath(String smallGroupPath) {

    return Optional.ofNullable(queryFactory
        .selectFrom(smallGroup).distinct()
          .leftJoin(smallGroup.leader, member).fetchJoin()
          .leftJoin(smallGroup.smallGroupInterests, smallGroupInterest).fetchJoin()
          .leftJoin(smallGroupInterest.interest, interest).fetchJoin()
        .where(smallGroup.path.eq(smallGroupPath))
        .fetchOne());
  }
}
