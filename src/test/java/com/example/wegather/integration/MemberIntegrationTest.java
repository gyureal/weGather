package com.example.wegather.integration;

import static com.example.wegather.member.domain.vo.MemberType.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.dto.AddressRequest.AddressRequestBuilder;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.MemberType;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.io.File;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

@DisplayName("회원 통합테스트")
public class MemberIntegrationTest extends IntegrationTest {

  private static final String DEFAULT_IMAGE_NAME = "default.jpg";
  @Autowired
  MemberRepository memberRepository;

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
        .ignoringFields("id", "profileImage")
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
    int size = 2;
    int page = 0;

    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/members")
        .then().log().all()
        .extract();

    //then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    // 리턴 객체 검증
    List<MemberDto> list = response.jsonPath().getList("content", MemberDto.class);
    assertThat(list).hasSize(2);
    assertThat(list).usingRecursiveComparison().isEqualTo(List.of(member01, member02));

    // 페이징 관련 리턴값 검증
    int pageSize = (int) response.path("pageable.pageSize");
    int pageNumber = (int) response.path("pageable.pageNumber");
    assertThat(pageSize).isEqualTo(size);
    assertThat(pageNumber).isEqualTo(page);
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

  @Test
  @DisplayName("회원 프로필 이미지를 수정합니다.")
  @Rollback(value = false)
  void updateProfileImageSuccessfully() {
    // given
    MultiPartSpecification file = new MultiPartSpecBuilder("111,222".getBytes())
        .mimeType(MediaType.TEXT_PLAIN_VALUE)
        .controlName("profileImage")
        .fileName("image.jpg")
        .build();

    Long id = member01.getId();

    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .pathParam("id", id)
        .multiPart(file)
        .when().post("/members/{id}/image")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    Member findMember = memberRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("회원이 없습니다."));
    System.out.println("storedImage : " + findMember.getProfileImage().getValue());
    assertThat(findMember.getProfileImage().getValue()).isNotEqualTo(DEFAULT_IMAGE_NAME);
  }

  @Test
  @DisplayName("회원의 주소를 수정합니다.")
  void changeAddressSuccessfully() {
    // given
    AddressRequest addressRequest = AddressRequest.builder()
        .streetAddress("서울시 중앙대로 123-123")
        .longitude(203.123)
        .latitude(123.123)
        .build();
    // when
    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .pathParam("id", member01.getId())
        .body(addressRequest)
        .contentType(ContentType.JSON)
        .when().post("/members/{id}/address")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
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
        .build();

    return RestAssured
        .given().body(request).contentType(ContentType.JSON)
        .when().post("/members")
        .then().extract().as(MemberDto.class);
  }
}
