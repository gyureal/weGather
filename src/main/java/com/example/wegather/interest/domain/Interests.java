package com.example.wegather.interest.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Interests implements Serializable {
  private static final String DELIMITER = "/";    // 구분자

  private Set<String> value = new HashSet<>();

  private Interests(Set<String> interests) {
    value = interests;
  }

  private Interests(String interests) {
    value = convertStringToSet(interests);
  }

  public static Interests of(String strValue) {
    return new Interests(strValue);
  }
  public static Interests of(Set<String> set) {
    return new Interests(set);
  }

  public static Interests of(List<String> list) {
    return new Interests(new HashSet<>(list));
  }

  /**
   * 컬렉션을 문자열 값으로 변환하여 반환합니다.
   * ex) Set of {배구, 야구, 농구} -> String of "배구/야구/농구"
   */
  public String convertToString() {
    return String.join(DELIMITER, value);
  }

  private static Set<String> convertStringToSet(String strValue) {
    if (strValue.isEmpty()) {
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(strValue.split(DELIMITER)));
  }

  /**
   * 관심사를 추가합니다.
   * @param interest
   */
  public void add(String interest) {
    value.add(interest);
  }

  /**
   * 관심사 목록에서 특정 관심사를 삭제합니다.
   * @param interest
   * @return Boolean
   *   true - 관심사 삭제에 성공한 경우
   *   false - 해당 관심사가 목록 내에 없는 경우
   */
  public boolean remove(String interest) {
    return value.remove(interest);
  }

  /**
   * 관심사가 있는지 반환합니다.
   * @param interest
   * @return
   */
  public boolean contains(String interest) {
    return value.contains(interest);
  }

  public List<String> convertToList() {
    return new ArrayList<>(value);
  }
}

