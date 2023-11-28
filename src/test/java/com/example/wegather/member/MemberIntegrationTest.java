package com.example.wegather.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.wegather.IntegrationTest;
import com.example.wegather.auth.AuthControllerTest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.EditProfileImageRequest;
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
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("회원 통합테스트")
class MemberIntegrationTest extends IntegrationTest {

  @Autowired
  MemberRepository memberRepository;
  private static final String memberPassword = "1234";
  MemberDto member01;
  MemberDto member02;
  MemberDto member03;
  @MockBean
  StoreFile storeFile;


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
    RestAssured.given().log().ifValidationFails().spec(spec)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/members")
        .then().log().ifValidationFails()
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
        RestAssured.given().log().ifValidationFails().spec(spec)
        .contentType(ContentType.JSON)
        .when().get("/members/{id}", member01.getId())
        .then().log().ifValidationFails()
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
        RestAssured.given().log().ifValidationFails().spec(spec)
        .contentType(ContentType.JSON)
        .when().delete("/members/{id}", member01.getId())
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("회원 프로필 이미지를 수정합니다.")
  void editProfileImageSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    EditProfileImageRequest request = EditProfileImageRequest.builder()
        .image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIA")
        .originalImageName("test.jpg")
        .build();

    // 이미지 저장 mock
    String storeFileName = "storeFileName";
    given(storeFile.storeFile(any(), any())).willReturn(UploadFile.of("", storeFileName));

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .contentType(ContentType.JSON)
        .body(request)
        .when().post("/members/profile/image")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    // 호출 검증
    then(storeFile).should().storeFile(any(), any());

    Member member = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));
    assertThat(member.getProfileImage()).isEqualTo(storeFileName);
  }

  @Test
  @DisplayName("MultipartFile 형식의 입력값으로 회원 프로필 이미지를 수정합니다.")
  void editProfileImageMultipartFileSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    // 가짜 파일
    String fakeFileContent = "This is a fake file content.";
    byte[] fakeFileBytes = fakeFileContent.getBytes();
    // 이미지 저장 mock
    String storeFileName = "storeFileName";
    given(storeFile.storeFile(any())).willReturn(UploadFile.of("", storeFileName));

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .contentType(ContentType.MULTIPART)
        .multiPart("file", "fake-file.txt", fakeFileBytes)
        .when().post("/members/profile/image/v2")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    // 호출 검증
    then(storeFile).should().storeFile(any());

    Member member = memberRepository.findById(member01.getId())
        .orElseThrow(() -> new RuntimeException("test fail"));
    assertThat(member.getProfileImage()).isEqualTo(storeFileName);
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
        RestAssured.given().log().ifValidationFails().spec(spec)
            .queryParam("interestId", interest1.getId())
            .contentType(ContentType.JSON)
            .when().post("/members/{id}/interests", member01.getId())
            .then().log().ifValidationFails()
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
        RestAssured.given().log().ifValidationFails()
            .spec(spec)
            .queryParam("interestId", interest1.getId())
            .contentType(ContentType.JSON)
            .when().delete("/members/{id}/interests", member01.getId())
            .then().log().ifValidationFails()
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
        .build();

    return AuthControllerTest.signUp(request).as(MemberDto.class);
  }
}
