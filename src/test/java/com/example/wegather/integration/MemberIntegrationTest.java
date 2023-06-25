package com.example.wegather.integration;

import static com.example.wegather.member.domain.vo.MemberType.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.member.domain.vo.MemberType;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 통합테스트")
public class MemberIntegrationTest extends IntegrationTest {

  MemberDto member01;
  MemberDto member02;
  MemberDto member03;

  @BeforeEach
  void init() {
    member01 = insertMember("test01", "김지유", USER);
    member02 = insertMember("test02", "김진주", USER);
    member03 = insertMember("test03", "박세미", USER);
  }

  @Test
  @DisplayName("회원을 생성합니다.")
  void joinMemberSuccessfully() {
    JoinMemberRequest request = JoinMemberRequest.builder()
        .username("test1")
        .password("password")
        .name("김지유")
        .phoneNumber("010-1234-1234")
        .streetAddress("서울시 강남구 백양대로 123-12")
        .longitude(123.12312)
        .latitude(23.131)
        .memberType(USER)
        .profileImage("/image/test/1")
        .build();

    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/members")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    MemberDto memberDto = response.body().as(MemberDto.class);
    assertThat(memberDto)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .ignoringActualNullFields()
        .isEqualTo(request);
  }

  @Test
  @DisplayName("회원 ID가 이미 존재하는 경우 예외를 던집니다.")
  void joinMemberFailWhenUsernameAlreadyExists() {
    // given
    String username = "duplicate1";
    insertMember(username, "김지유", USER);

    JoinMemberRequest request = JoinMemberRequest.builder()
        .username(username)
        .password("password")
        .name("김지유")
        .phoneNumber("010-1234-1234")
        .streetAddress("서울시 강남구 백양대로 123-12")
        .longitude(123.12312)
        .latitude(23.131)
        .memberType(USER)
        .profileImage("/image/test/1")
        .build();

    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/members")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("전체 회원을 조회합니다.")
  void readAllMembersSuccessfully() {
    // given
    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .contentType(ContentType.JSON)
        .when().get("/members")
        .then().log().all()
        .extract();

    //then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<MemberDto> list = response.body().jsonPath().getList(".", MemberDto.class);
    assertThat(list).hasSize(3);
    assertThat(list).usingRecursiveComparison().isEqualTo(List.of(member01, member02, member03));
  }

  @Test
  @DisplayName("id로 회원을 조회합니다.")
  void readOneMemberByIdSuccessfully() {
    // given
    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .pathParam("id", member01.getId())
        .contentType(ContentType.JSON)
        .when().get("/members/{id}")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    assertThat(response.body().as(MemberDto.class))
        .usingRecursiveComparison().isEqualTo(member01);
  }

  @Test
  @DisplayName("id로 관심사를 삭제합니다.")
  void deleteMemberByIdSuccessfully() {
    // given
    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .pathParam("id", member01.getId())
        .contentType(ContentType.JSON)
        .when().delete("/members/{id}")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }


  private MemberDto insertMember(String username, String name, MemberType memberType) {
    JoinMemberRequest request = JoinMemberRequest.builder()
        .username(username)
        .password("password")
        .name(name)
        .phoneNumber("010-1234-1234")
        .streetAddress("서울시 강남구 백양대로 123-12")
        .longitude(123.12312)
        .latitude(23.131)
        .memberType(memberType)
        .profileImage("/image/test/1")
        .build();

    return RestAssured
        .given().body(request).contentType(ContentType.JSON)
        .when().post("/members")
        .then().extract().as(MemberDto.class);
  }
}
