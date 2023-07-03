package com.example.wegather.group.domain.repotitory;

import static com.example.wegather.group.domain.QGroup.*;
import static com.example.wegather.member.domain.QMember.*;

import com.example.wegather.group.domain.Group;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryQuerydsl {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Group> search(GroupSearchCondition cond, Pageable pageable) {
    List<Group> content = queryFactory
        .selectFrom(group)
        .leftJoin(group.leader, member)
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
    return groupName != null ? group.name.contains(groupName) : null;
  }

  private BooleanExpression streetAddressContains(String streetAddress) {
    return streetAddress != null ? group.address.streetAddress.contains(streetAddress) : null;
  }

  private BooleanExpression leaderUsernameLike(String leaderUsername) {
    return leaderUsername != null ? group.leader.username.value.like(leaderUsername) : null;
  }

  private BooleanExpression maxMemberCountGoe(Integer maxMemberCountFrom) {
    return maxMemberCountFrom != null ? group.maxMemberCount.value.goe(maxMemberCountFrom) : null;
  }

  private BooleanExpression maxMemberCountLt(Integer maxMemberCountTo) {
    return maxMemberCountTo != null ? group.maxMemberCount.value.lt(maxMemberCountTo) : null;
  }
}
