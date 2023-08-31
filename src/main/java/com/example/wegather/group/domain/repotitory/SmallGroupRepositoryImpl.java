package com.example.wegather.group.domain.repotitory;

import static com.example.wegather.group.domain.entity.QSmallGroup.smallGroup;
import static com.example.wegather.group.domain.entity.QSmallGroupInterest.*;
import static com.example.wegather.member.domain.entity.QMember.member;

import com.example.wegather.group.domain.entity.QSmallGroupInterest;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
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
  public Page<SmallGroup> search(SmallGroupSearchCondition cond, Pageable pageable) {
    List<SmallGroup> content = queryFactory
        .selectFrom(smallGroup)
        .join(smallGroup.leader, member)
        .where(
            groupNameContains(cond.getGroupName()),
            streetAddressContains(cond.getStreetAddress()),
            leaderUsernameLike(cond.getLeaderUsername()),
            maxMemberCountGoe(cond.getMaxMemberCountFrom()),
            maxMemberCountLt(cond.getMaxMemberCountTo()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return PageableExecutionUtils.getPage(content, pageable, content::size);
  }

  private BooleanExpression groupNameContains(String groupName) {
    return groupName != null ? smallGroup.name.contains(groupName) : null;
  }

  private BooleanExpression streetAddressContains(String streetAddress) {
    return streetAddress != null ? smallGroup.address.streetAddress.contains(streetAddress) : null;
  }

  private BooleanExpression leaderUsernameLike(String leaderUsername) {
    return leaderUsername != null ? smallGroup.leader.username.value.like(leaderUsername) : null;
  }

  private BooleanExpression maxMemberCountGoe(Integer maxMemberCountFrom) {
    return maxMemberCountFrom != null ? smallGroup.maxMemberCount.value.goe(maxMemberCountFrom) : null;
  }

  private BooleanExpression maxMemberCountLt(Integer maxMemberCountTo) {
    return maxMemberCountTo != null ? smallGroup.maxMemberCount.value.lt(maxMemberCountTo) : null;
  }

  @Override
  public Optional<SmallGroup> findWithInterestById(Long smallGroupId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(smallGroup).distinct()
          .leftJoin(smallGroup.leader, member).fetchJoin()
          .leftJoin(smallGroup.smallGroupInterests, smallGroupInterest).fetchJoin()
        .where(smallGroup.id.eq(smallGroupId))
        .fetchOne());
  }
}
