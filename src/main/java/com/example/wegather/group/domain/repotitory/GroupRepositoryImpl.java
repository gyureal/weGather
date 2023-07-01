package com.example.wegather.group.domain.repotitory;

import static com.example.wegather.group.domain.QGroup.*;

import com.example.wegather.group.domain.Group;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryQuerydsl {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Group> search(GroupSearchCondition cond) {
    return queryFactory
        .selectFrom(group)
        .where(
            groupNameContains(cond.getGroupName()),
            streetAddressContains(cond.getStreetAddress()),
            leaderUsernameLike(cond.getLeaderUsername()),
            maxMemberCountGoe(cond.getMaxMemberCountFrom()),
            maxMemberCountLt(cond.getMaxMemberCountTo()))
        .fetch();
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
