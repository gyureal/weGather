package com.example.wegather.integration;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("관심사 통합테스트")
public class InterestIntegrationTest extends IntegrationTest {

  @Test
  @DisplayName("관심사를 생성합니다.")
  void createInterestSuccessfully() {
    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName("축구")
        .build();

    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    assertThat(response.body().as(InterestDto.class).getName())
        .isEqualTo(request.getInterestName());
  }

  @Test
  @DisplayName("관심사 명이 이미 존재하는 경우 예외를 던집니다.")
  void createInterestFailWhenNameAlreadyExists() {
    // given
    String interestName = "아구";
    insertInterest(interestName);

    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName(interestName)
        .build();

    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  private InterestDto insertInterest(String interestName) {
    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName(interestName)
        .build();

    return RestAssured
        .given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then().log().all()
        .extract().as(InterestDto.class);
  }
}
