package com.example.wegather.group.domain.repotitory;

import static com.example.wegather.group.domain.entity.QSmallGroup.smallGroup;
import static com.example.wegather.group.domain.entity.QSmallGroupInterest.*;
import static com.example.wegather.group.domain.entity.QSmallGroupMember.smallGroupMember;
import static com.example.wegather.interest.domain.QInterest.*;
import static com.example.wegather.member.domain.entity.QMember.member;

import com.example.wegather.group.domain.entity.QSmallGroupMember;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class SmallGroupRepositoryImpl extends QuerydslRepositorySupport implements SmallGroupRepositoryQuerydsl {

  public SmallGroupRepositoryImpl(EntityManager entityManager) {
    super(SmallGroup.class);
    setEntityManager(entityManager);
  }

  @Override
  public Page<SmallGroup> search(String keyword, Pageable pageable) {
    JPQLQuery<SmallGroup> query = from(smallGroup).distinct()
        .join(smallGroup.leader, member).fetchJoin()
        .leftJoin(smallGroup.smallGroupInterests, smallGroupInterest).fetchJoin()
        .leftJoin(smallGroupInterest.interest, interest).fetchJoin()
        .leftJoin(smallGroup.members, smallGroupMember).fetchJoin()
        .leftJoin(smallGroupMember.member, member).fetchJoin()
        .where(
            groupNameContains(keyword)
        );

    JPQLQuery<SmallGroup> pageableQuery = getQuerydsl().applyPagination(pageable, query);
    QueryResults<SmallGroup> fetchResult = pageableQuery.fetchResults();
    return new PageImpl<>(fetchResult.getResults(), pageable, fetchResult.getTotal());
  }

  private BooleanExpression groupNameContains(String groupName) {
    return groupName != null ? smallGroup.name.contains(groupName) : null;
  }

  @Override
  public Optional<SmallGroup> findWithInterestByPath(String smallGroupPath) {

    return Optional.ofNullable(
        from(smallGroup).distinct()
          .leftJoin(smallGroup.leader, member).fetchJoin()
          .leftJoin(smallGroup.smallGroupInterests, smallGroupInterest).fetchJoin()
          .leftJoin(smallGroupInterest.interest, interest).fetchJoin()
        .where(smallGroup.path.eq(smallGroupPath))
        .fetchOne());
  }
}
