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
  @DisplayName("관심사를 추가합니다.")
  void addInterest_success() {
    Set<String> interestSet = new HashSet<>(Arrays.asList("배구", "야구", "농구"));
    Interests interests = Interests.of(interestSet);

    interests.add("달리기");

    Set<String> set = interests.getValue();
    assertThat(set).contains("달리기");
  }

  @Test
  @DisplayName("관심사를 삭제합니다.")
  void removeInterest_success() {
    Set<String> interestSet = new HashSet<>(Arrays.asList("배구", "야구", "농구"));
    Interests interests = Interests.of(interestSet);

    interests.remove("배구");

    Set<String> set = interests.getValue();
    assertThat(set).hasSize(2);
  }
}
