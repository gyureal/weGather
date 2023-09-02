package com.example.wegather.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("소모임 가입 통합 테스트")
class SmallGroupJoinIntegrationTest extends IntegrationTest{
  private static final String memberPassword = "1234";
  private MemberDto member01;
  private MemberDto member02;
  private SmallGroupDto group01;

  @BeforeEach
  void initData() {
    member01 = insertMember("member01", memberPassword, MemberType.ROLE_USER);
    member02 = insertMember("member02", memberPassword, MemberType.ROLE_USER);
    group01 = insertSmallGroup("group01", 100, member01);
  }

  @Test
  @DisplayName("소모임 가입 요청에 성공합니다.")
  void smallGroupJoinRequest_successfully() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;

    ExtractableResponse<Response> response = requestSmallGroupJoinRequest(smallGroup, joinMember);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("소모임장이어서 소모임 가입 요청에 실패합니다.")
  void smallGroupJoinRequest_fail_because_groupLeader_request() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member01;

    ExtractableResponse<Response> response = requestSmallGroupJoinRequest(
        smallGroup, joinMember);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  private static ExtractableResponse<Response> requestSmallGroupJoinRequest(
      SmallGroupDto smallGroup, MemberDto joinMember) {
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(joinMember.getUsername(), memberPassword)
        .pathParam("id", smallGroup.getId())
        .when().post("smallGroups/{id}/join/requests")
        .then().log().all().extract();
    return response;
  }

  private SmallGroupDto insertSmallGroup(String groupName, Integer maxMemberCount, MemberDto member) {
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .groupName(groupName)
        .description("테스트입니다.")
        .streetAddress("서울특별시 중구 세종대로 125")
        .maxMemberCount(maxMemberCount)
        .build();

    return RestAssured.given().log().all()
        .auth().basic(member.getUsername(), memberPassword)
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
        .memberType(memberType)
        .build();

    return RestAssured.given().body(request).contentType(ContentType.JSON)
        .when().post("/members")
        .then().extract().as(MemberDto.class);
  }
}
