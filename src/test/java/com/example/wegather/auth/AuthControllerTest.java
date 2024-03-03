package com.example.wegather.auth;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.example.wegather.auth.dto.SignInRequest;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.IntegrationTest;
import com.example.wegather.global.mail.EmailMessage;
import com.example.wegather.global.mail.EmailService;
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
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AuthControllerTest extends IntegrationTest {

  @Autowired
  MemberRepository memberRepository;
  @MockBean
  EmailService emailService;

  SignUpRequest signUpRequest = SignUpRequest.builder()
      .username("test01")
      .password("password")
      .email("test01@example.com")
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
    BDDMockito.then(emailService).should().sendEmail(any(String.class), any(EmailMessage.class));
  }

  @Test
  @DisplayName("회원가입 시, username 이 이미 존재하는 경우 예외를 던집니다.")
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
  @DisplayName("로그인 시, username 이 존재하지 않는 경우 예외를 던집니다.")
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

  @Test
  @DisplayName("이메일 인증에 성공합니다.")
  void email_token_check_success() {
    signUp(signUpRequest);
    Member member = findByUsername(signUpRequest.getUsername());

    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .when().queryParam("email", member.getEmail()).queryParam("token", member.getEmailCheckToken())
        .post("/api/check-email-token")
        .then().log().ifValidationFails().extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    member = findByUsername(signUpRequest.getUsername());
    assertThat(member.isEmailVerified()).isTrue();
    assertThat(member.getJoinedAt()).isNotNull();
  }

  public static ExtractableResponse<Response> signUp(SignUpRequest signUpRequest) {
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .body(signUpRequest).contentType(ContentType.JSON)
        .when().post("/api/sign-up")
        .then().log().ifValidationFails()
        .extract();
    return response;
  }

  private ExtractableResponse<Response> requestSignIn(SignInRequest signInRequest) {
    return RestAssured.given().log().ifValidationFails()
        .body(signInRequest).contentType(ContentType.JSON)
        .when().post("/api/sign-in")
        .then().log().ifValidationFails()
        .extract();
  }

  public static RequestSpecification signIn(String username, String password) {
    SignInRequest signInRequest = SignInRequest.of(username, password);

    String sessionId = RestAssured.given().log().ifValidationFails()
        .body(signInRequest).contentType(ContentType.JSON)
        .when().post("/api/sign-in")
        .sessionId();
    return new RequestSpecBuilder().setSessionId(sessionId).build();
  }

  public Member findByUsername(String username) {
    return memberRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("member not found"));
  }
}
