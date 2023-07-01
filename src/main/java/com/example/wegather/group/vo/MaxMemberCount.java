package com.example.wegather.group.vo;

import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소그룹 엔티티의 최대 멤버수 VO
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaxMemberCount {
  private Integer value;

  public MaxMemberCount(Integer value) {
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
