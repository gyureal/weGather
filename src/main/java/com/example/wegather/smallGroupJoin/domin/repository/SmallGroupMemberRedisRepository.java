package com.example.wegather.smallGroupJoin.domin.repository;

public interface SmallGroupMemberRedisRepository {

  /**
   * 해당 소모임에 회원을 추가합니다.
   */
  void addMemberInSmallGroup(Long smallGroupId, String username ,Integer maxCount);
}
