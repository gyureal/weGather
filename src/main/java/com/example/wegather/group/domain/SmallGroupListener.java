package com.example.wegather.group.domain;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmallGroupListener {
  /**
   * 회원 엔티티를 DB에 저장하기 전, 실행됩니다.
   *   관심사 : DB에 반영하기 전, 컬렉션으로 된 관심사 값을 String 으로 변환하여 저장합니다.
   * @param smallGroup
   */
  @PrePersist
  @PreUpdate
  private void beforeAnyUpdate(SmallGroup smallGroup) {
    if (!smallGroup.isInterestsNull()) {
      smallGroup.setInterestToString();
    }
  }

  /**
   * 회원 엔티티를 조회 시 실행됩니다.
   *    관심사 : 엔티티를 로드한 후, String 타입의 관심사 값을 컬렉션으로 변경하여 저장합니다.
   * @param smallGroup
   */
  @PostLoad
  private void afterLoaded(SmallGroup smallGroup) {
    if (!smallGroup.isInterestsNull()) {
      smallGroup.setInterestsFromString();
    }
  }
}
