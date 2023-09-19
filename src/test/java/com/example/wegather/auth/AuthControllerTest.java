package com.example.wegather.auth;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.auth.dto.SignInRequest;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.IntegrationTest;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthControllerTest extends IntegrationTest {

  @Autowired
  MemberRepository memberRepository;

  SignUpRequest signUpRequest = SignUpRequest.builder()
      .username("test01")
      .password("password")
      .email("테스트유져")
      .build();

  @Test
  @DisplayName("회원가입을 합니다.")
  void signUp_success() {
    // when
    ExtractableResponse<Response> response = signUp(signUpRequest);

    //then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    Member member = findByUsername(signUpRequest.getUsername());
    assertThat(member.getUsername()).isEqualTo(signUpRequest.getUsername());
    assertThat(member.getEmail()).isEqualTo(signUpRequest.getEmail());
    assertThat(member.getEmailCheckToken()).isNotEmpty();
    assertThat(member.getEmailCheckTokenGeneratedAt()).isNotNull();
  }

  @Test
  @DisplayName("username 이 이미 존재하는 경우 예외를 던집니다.")
  void signUp_fail_when_username_already_exists() {
    // given
    signUp(signUpRequest);

    // when
    ExtractableResponse<Response> response = signUp(signUpRequest);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("로그인을 성공합니다.")
  void signIn_success() {
    // given
    signUp(signUpRequest);
    SignInRequest signInRequest =
        SignInRequest.of(signUpRequest.getUsername(), signUpRequest.getPassword());

    // when
    ExtractableResponse<Response> response = requestSignIn(signInRequest);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("username 을 찾을 수 없어 로그인에 실패합니다.")
  void signIn_fail_because_username_not_found() {
    // given
    signUp(signUpRequest);
    SignInRequest signInRequest =
        SignInRequest.of("notFoundUsername", signUpRequest.getPassword());

    // when
    ExtractableResponse<Response> response = requestSignIn(signInRequest);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("password 가 달라서 로그인에 실패합니다.")
  void signIn_fail_because_password_invalid() {
    // given
    signUp(signUpRequest);
    SignInRequest signInRequest =
        SignInRequest.of(signUpRequest.getUsername(), "fail_password");

    // when
    ExtractableResponse<Response> response = requestSignIn(signInRequest);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  public static ExtractableResponse<Response> signUp(SignUpRequest signUpRequest) {
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .body(signUpRequest).contentType(ContentType.JSON)
        .when().post("/sign-up")
        .then().log().all()
        .extract();
    return response;
  }

  private ExtractableResponse<Response> requestSignIn(SignInRequest signInRequest) {
    return RestAssured.given().log().all()
        .body(signInRequest).contentType(ContentType.JSON)
        .when().post("/sign-in")
        .then().log().all()
        .extract();
  }

  public static RequestSpecification signIn(String username, String password) {
    SignInRequest signInRequest = SignInRequest.of(username, password);

    String sessionId = RestAssured.given().log().all()
        .body(signInRequest).contentType(ContentType.JSON)
        .when().post("/sign-in")
        .sessionId();
    return new RequestSpecBuilder().setSessionId(sessionId).build();
  }

  public Member findByUsername(String username) {
    return memberRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("member not found"));
  }

}
