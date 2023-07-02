package com.example.wegather.group.vo;

import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

/**
 * 소그룹 엔티티의 최대 멤버수 VO
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaxMemberCount {

  private static final String MAX_MEMBER_COUNT_CANNOT_NULL = "최대 회원 수는 null 일 수 없습니다.";
  private static final String MAX_MEMBER_COUNT_MUST_LARGER_THAN_ZERO = "최대 회원 수는 0보다 커야 합니다.";
  private Integer value;

  public MaxMemberCount(Integer value) {
    Assert.notNull(value, MAX_MEMBER_COUNT_CANNOT_NULL);
    if (value < 0) {
      throw new IllegalArgumentException(MAX_MEMBER_COUNT_MUST_LARGER_THAN_ZERO);
    }
    this.value = value;
  }

  public static MaxMemberCount of(Integer value) {
    return new MaxMemberCount(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaxMemberCount that = (MaxMemberCount) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
