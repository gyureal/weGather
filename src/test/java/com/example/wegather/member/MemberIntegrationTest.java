package com.example.wegather.member;

import static com.example.wegather.global.vo.MemberType.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.IntegrationTest;
import com.example.wegather.auth.AuthControllerTest;
import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("회원 통합테스트")
class MemberIntegrationTest extends IntegrationTest {

  @Autowired
  MemberRepository memberRepository;
  private static final String memberPassword = "1234";
  MemberDto member01;
  MemberDto member02;
  MemberDto member03;


  @BeforeEach
  void init() {
    member01 = insertTestMember("test01","testUser1@gmail.com" ,memberPassword);
    member02 = insertTestMember("test02", "testUser2@gmail.com" ,memberPassword);
    member03 = insertTestMember("test03", "testUser3@gmail.com", memberPassword);
  }

  @Test
  @DisplayName("전체 회원을 조회합니다.")
  void readAllMembersSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    int size = 2;
    int page = 0;

    // when
    ExtractableResponse<Response> response =
    RestAssured.given().log().all().spec(spec)
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
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    // when
    ExtractableResponse<Response> response =
        RestAssured.given().log().all().spec(spec)
        .contentType(ContentType.JSON)
        .when().get("/members/{id}", member01.getId())
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    assertThat(response.body().as(MemberDto.class))
        .usingRecursiveComparison().isEqualTo(member01);
  }

  @Test
  @DisplayName("id로 회원을 삭제합니다.")
  void deleteMemberByIdSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    // when
    ExtractableResponse<Response> response =
        RestAssured.given().log().all().spec(spec)
        .contentType(ContentType.JSON)
        .when().delete("/members/{id}", member01.getId())
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("회원 프로필 이미지를 수정합니다.")
  void updateProfileImageSuccessfully() {
//    String controlName = "profileImage";
//    String fileName = "image.jpg";
//    String mediaType = MediaType.TEXT_PLAIN_VALUE;
//    byte[] bytes = "111,222".getBytes();
//
//    // given
//    MultiPartSpecification file = new MultiPartSpecBuilder("111,222".getBytes())
//        .mimeType(MediaType.TEXT_PLAIN_VALUE)
//        .controlName("profileImage")
//        .fileName("image.jpg")
//        .build();
//
//    Long id = member01.getId();
//
//    // when
//    ExtractableResponse<MockMvcResponse> response =
//        given().log().all()
//        .multiPart(controlName, fileName, bytes, mediaType)
//        .when().put("/members/{id}/image", id)
//        .then().log().all()
//        .extract();
//
//    // then
//    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
//    Member findMember = memberRepository.findById(id)
//        .orElseThrow(() -> new RuntimeException("회원이 없습니다."));
//    String storedImage = findMember.getProfileImage().getValue();
//    System.out.println("storedImage : " + storedImage);
//    assertThat(storedImage).isNotEqualTo(DEFAULT_IMAGE_NAME);
//
//    // 이미지 삭제
//
//    given().log().all()
//        .when().delete("/images/{filename}", storedImage)
//        .then().log().all();
  }

  @Test
  @DisplayName("회원의 관심사를 추가합니다.")
  void addMemberInterestsSuccessfully() {
    // given
    InterestDto interest1 = insertInterest("공부", member01);

    // when
    ExtractableResponse<Response> response = requestAddMemberInterest(interest1, member01);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<InterestDto> interestList = response.jsonPath().getList(".", InterestDto.class);
    assertThat(interestList).extracting("name")
        .contains(interest1.getName());
  }

  private ExtractableResponse<Response> requestAddMemberInterest(InterestDto interest1, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);
    ExtractableResponse<Response> response =
        RestAssured.given().log().all().spec(spec)
            .queryParam("interestId", interest1.getId())
            .contentType(ContentType.JSON)
            .when().post("/members/{id}/interests", member01.getId())
            .then().log().all()
            .extract();
    return response;
  }

  @Test
  @DisplayName("회원의 관심사를 삭제합니다.")
  void removeMemberInterestsSuccessfully() {
    // given
    InterestDto interest1 = insertInterest("공부", member01);
    requestAddMemberInterest(interest1, member01);
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    // when
    ExtractableResponse<Response> response =
        RestAssured.given().log().all()
            .spec(spec)
            .queryParam("interestId", interest1.getId())
            .contentType(ContentType.JSON)
            .when().delete("/members/{id}/interests", member01.getId())
            .then().log().all()
            .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<InterestDto> interestList = response.jsonPath().getList(".", InterestDto.class);
    assertThat(interestList).extracting("name")
        .doesNotContain(interest1.getName());
  }

  private InterestDto insertInterest(String interestName, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);

    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName(interestName)
        .build();

    return RestAssured.given().spec(spec)
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/interests")
        .then()
        .extract().as(InterestDto.class);
  }


  private MemberDto insertTestMember(String username, String email ,String password) {
    SignUpRequest request = SignUpRequest.builder()
        .username(username)
        .password(password)
        .email(email)
        .phoneNumber("010-1234-1234")
        .build();

    return AuthControllerTest.signUp(request).as(MemberDto.class);
  }
}
