package com.example.wegather.interest.domain;


import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("관심사 엔티티 테스트")
class InterestsTest {

  @Test
  @DisplayName("문자열 데이터로 부터 관심사 데이터를 세팅합니다.")
  void setInterestsFromString() {
    String strInput = "배구/야구/농구";

    Interests interests = Interests.of(strInput);
    interests.setFromString();

    Set<String> set = interests.getInterests();
    assertThat(set).containsExactlyInAnyOrder("배구", "야구", "농구");
  }

  @Test
  @DisplayName("문자열 데이터로 부터 관심사 데이터를 세팅합니다 - 빈값인 경우")
  void setInterestsFromStringWhenEmptyString() {
    String strInput = "";

    Interests interests = Interests.of(strInput);
    interests.setFromString();

    Set<String> set = interests.getInterests();
    assertThat(set).isEmpty();
  }

  @Test
  @DisplayName("리스트로된 관심사 값을 문자열로 변환하여 세팅합니다.")
  void setInterestsToString() {
    Set<String> interestSet = Set.of("배구", "야구", "농구");

    Interests interests = Interests.of(interestSet);
    interests.setToString();

    String strValue = interests.getStrValue();
    assertThat(strValue).contains("배구");
    assertThat(strValue).contains("야구");
    assertThat(strValue).contains("농구");
  }

  @Test
  @DisplayName("리스트로된 관심사 값을 문자열로 변환하여 세팅합니다. - 빈 값인 경우")
  void setInterestsToStringWhenEmpty() {
    Set<String> interestSet = new HashSet<>();

    Interests interests = Interests.of(interestSet);
    interests.setToString();

    String strValue = interests.getStrValue();
    assertThat(strValue).isEqualTo("");
  }

  @Test
  @DisplayName("관심사를 추가합니다.")
  void addInterest_success() {
    Set<String> interestSet = new HashSet<>(Arrays.asList("배구", "야구", "농구"));
    Interests interests = Interests.of(interestSet);

    interests.add("달리기");

    Set<String> set = interests.getInterests();
    assertThat(set).contains("달리기");
  }

  @Test
  @DisplayName("관심사를 삭제합니다.")
  void removeInterest_success() {
    Set<String> interestSet = new HashSet<>(Arrays.asList("배구", "야구", "농구"));
    Interests interests = Interests.of(interestSet);

    interests.remove("배구");

    Set<String> set = interests.getInterests();
    assertThat(set).hasSize(2);
  }
}
