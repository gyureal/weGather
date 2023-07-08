package com.example.wegather.integration;

import static org.assertj.core.api.Assertions.*;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import com.example.wegather.smallGroupJoin.dto.SmallGroupMemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("소모임 가입 통합테스트")
public class SmallGroupJoinIntegrationTest extends IntegrationTest {

  private static final String memberPassword = "1234";

  private MemberDto member01;
  private MemberDto member02;
  private MemberDto member03;
  private SmallGroupDto group01;

  @BeforeEach
  void init() {
    member01 = insertMember("member01", memberPassword, MemberType.ROLE_USER);
    member02 = insertMember("member02", memberPassword, MemberType.ROLE_USER);
    member03 = insertMember("member03", memberPassword, MemberType.ROLE_USER);
    group01 = insertGroup("탁사모", member01.getUsername(),10, Arrays.asList("탁구"));
  }

  @Test
  @DisplayName("소모임 가입에 성공합니다.")
  void joinSmallGroupSuccessfully() {
    Long smallGroupId = group01.getId();
    String memberUsername = member01.getUsername();

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .pathParam("id", smallGroupId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/join")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("이미 가입한 소모임이어서 소모임 가입에 실패합니다.")
  void joinSmallGroupFailBecauseOfAlreadyJoined() {
    // given
    insertSmallGroupMember(group01, member01);

    Long smallGroupId = group01.getId();
    String memberUsername = member01.getUsername();

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .pathParam("id", smallGroupId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/join")
        .then().log().all()
        .extract();

    //then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("해당 소모임에 가입요청한 모든 회원 목록을 조회합니다.")
  void readSmallGroupJoinMemberSuccessfully() {
    // given
    insertSmallGroupMember(group01, member02);
    insertSmallGroupMember(group01, member03);

    String leaderUsername = member01.getUsername();

    Long groupId = group01.getId();
    int size = 5;
    int page = 0;

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(leaderUsername, memberPassword)
        .pathParam("id", groupId)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups/{id}/join")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<SmallGroupMemberDto> result = response.jsonPath().getList("content", SmallGroupMemberDto.class);
    assertThat(result).hasSize(2);

    // 페이징 관련 리턴값 검증
    int pageSize = (int) response.path("pageable.pageSize");
    int pageNumber = (int) response.path("pageable.pageNumber");
  }

  @Test
  @DisplayName("조회할 권한이 없어서 소모임 가입자 목록 조회에 실패합니다.")
  void readSmallGroupJoinMemberFailBecauseOfUnAuthorized() {
    // given
    insertSmallGroupMember(group01, member02);
    insertSmallGroupMember(group01, member03);

    String notLeader = member02.getUsername();

    Long groupId = group01.getId();
    int size = 5;
    int page = 0;

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(notLeader, memberPassword)
        .pathParam("id", groupId)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups/{id}/join")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("소모임장이 회원을 탈퇴 처리에 성공합니다.")
  void leaveMemberSuccessfully() {
    // given
    insertSmallGroupMember(group01, member02);

    String leaderUsername = member01.getUsername();
    Long groupId = group01.getId();
    Long memberId = member02.getId();

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(leaderUsername, memberPassword)
        .pathParam("id", groupId)
        .queryParam("memberId", memberId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/leave")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("탈퇴 처리할 회원이 소모임에 가입되어 있지 않아 실패합니다.")
  void leaveMemberFailBecauseMemberIsNotJoined() {
    // given
    insertSmallGroupMember(group01, member02);

    String leaderUsername = member01.getUsername();
    Long groupId = group01.getId();
    Long memberId = member03.getId();

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(leaderUsername, memberPassword)
        .pathParam("id", groupId)
        .queryParam("memberId", memberId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/leave")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("회원 탈퇴 기능을 호출한 회원이 탈퇴시킬 권한이 없어서 실패합니다.")
  void leaveMemberFailBecauseUnAuthorized() {
    // given
    insertSmallGroupMember(group01, member02);

    String unAuthorizedUser = member03.getUsername();
    Long groupId = group01.getId();
    Long memberId = member02.getId();

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(unAuthorizedUser, memberPassword)
        .pathParam("id", groupId)
        .queryParam("memberId", memberId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/leave")
        .then().log().all()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  private void insertSmallGroupMember(SmallGroupDto smallGroup, MemberDto member) {
    RestAssured.given().log().all()
        .auth().basic(member.getUsername(), memberPassword)
        .pathParam("id", smallGroup.getId())
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/join")
        .then().log().all();
  }

  private SmallGroupDto insertGroup(String groupName, String username ,Integer maxMemberCount, List<String> interests) {
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .groupName(groupName)
        .description("테스트입니다.")
        .streetAddress("서울특별시 중구 세종대로 125")
        .maxMemberCount(maxMemberCount)
        .interests(interests)
        .build();

    return RestAssured.given().log().all()
        .auth().basic(username, memberPassword)
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups")
        .then().log().ifStatusCodeIsEqualTo(HttpStatus.SC_CREATED)
        .extract().as(SmallGroupDto.class);
  }

  private MemberDto insertMember(String username, String password, MemberType memberType) {
    JoinMemberRequest request = JoinMemberRequest.builder()
        .username(username)
        .password(password)
        .name("testUser")
        .phoneNumber("010-1234-1234")
        .streetAddress("서울시 강남구 백양대로 123-12")
        .longitude(123.12312)
        .latitude(23.131)
        .memberType(memberType)
        .build();

    return RestAssured.given().body(request).contentType(ContentType.JSON)
        .when().post("/members")
        .then().extract().as(MemberDto.class);
  }
}
