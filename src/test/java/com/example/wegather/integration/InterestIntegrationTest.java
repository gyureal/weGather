package com.example.wegather.integration;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.*;

import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.response.ExtractableResponse;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("관심사 통합테스트")
public class InterestIntegrationTest extends IntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void initRestAssuredApplicationContext() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }

  @Test
  @DisplayName("관심사를 생성합니다.")
  @WithMockUser("USER")
  void createInterestSuccessfully() {
    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName("축구")
        .build();

    ExtractableResponse<MockMvcResponse> response =
        RestAssuredMockMvc.given().log().all()
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
  @WithMockUser("USER")
  void createInterestFailWhenNameAlreadyExists() {
    // given
    String interestName = "아구";
    insertInterest(interestName);

    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName(interestName)
        .build();

    // when
    ExtractableResponse<MockMvcResponse> response = given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("전체 관심사를 조회합니다.")
  @WithMockUser("USER")
  void readAllInterestSuccessfully() {
    // given
    InterestDto baseball = insertInterest("야구");
    InterestDto running = insertInterest("달리기");

    // when
    ExtractableResponse<MockMvcResponse> response =
        given().log().all()
        .contentType(ContentType.JSON)
        .when().get("/interests")
        .then().log().all()
        .extract();

    //then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<InterestDto> list = response.body().jsonPath().getList(".", InterestDto.class);
    assertThat(list).hasSize(2);
    assertThat(list).usingRecursiveComparison().isEqualTo(List.of(baseball, running));
  }

  @Test
  @DisplayName("id로 관심사를 조회합니다.")
  @WithMockUser("USER")
  void readOneInterestByIdSuccessfully() {
    InterestDto swimming = insertInterest("수영");

    ExtractableResponse<MockMvcResponse> response =
        RestAssuredMockMvc.given().log().all()
        .contentType(ContentType.JSON)
        .when().get("/interests/{id}", swimming.getId())
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    assertThat(response.body().as(InterestDto.class))
        .usingRecursiveComparison().isEqualTo(swimming);
  }

  @Test
  @DisplayName("id로 관심사를 삭제합니다.")
  @WithMockUser("USER")
  void deleteInterestByIdSuccessfully() {
    InterestDto swimming = insertInterest("수영");

    ExtractableResponse<MockMvcResponse> response =
        given().log().all()
        .contentType(ContentType.JSON)
        .when().delete("/interests/{id}", swimming.getId())
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }


  private InterestDto insertInterest(String interestName) {
    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName(interestName)
        .build();

    return given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then().log().all()
        .extract().as(InterestDto.class);
  }
}
