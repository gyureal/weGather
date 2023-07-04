package com.example.wegather.interest.domain;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(InterestsListener.class)
@Embeddable
public class Interests {
  private static final String DELIMITER = "/";    // 구분자
  private String strValue;
  @Transient
  private Set<String> interests = new HashSet<>();

  private Interests(String strValue) {
    this.strValue = strValue;
    interests = convertStringToSet(strValue);
  }

  private Interests(Set<String> set) {
    interests = set;
  }

  public static Interests of(String strValue) {
    return new Interests(strValue);
  }

  public static Interests of(Set<String> set) {
    return new Interests(set);
  }

  /**
   * 문자열로 된 관심사 값을 컬렉션으로 변환하여 저장합니다.
   * ex) String of "배구/야구/농구" -> List of {배구, 야구, 농구}
   */
  public void setFromString() {
    interests = convertStringToSet(strValue);
  }

  /**
   * 컬렉션을 문자열 값으로 변환하여 저장합니다.
   * ex) Set of {배구, 야구, 농구} -> String of "배구/야구/농구"
   */
  public void setToString() {
    strValue = String.join(DELIMITER, interests);
  }

  private Set<String> convertStringToSet(String strValue) {
    return new HashSet<>(Arrays.asList(strValue.split(DELIMITER)));
  }

  /**
   * 관심사를 추가합니다.
   * @param interest
   */
  public void add(String interest) {
    interests.add(interest);
  }

  /**
   * 관심사 목록에서 특정 관심사를 삭제합니다.
   * @param interest
   * @return Boolean
   *   true - 관심사 삭제에 성공한 경우
   *   false - 해당 관심사가 목록 내에 없는 경우
   */
  public boolean remove(String interest) {
    return interests.remove(interest);
  }

  /**
   * 관심사가 있는지 반환합니다.
   * @param interest
   * @return
   */
  public boolean contains(String interest) {
    return interests.contains(interest);
  }
}

