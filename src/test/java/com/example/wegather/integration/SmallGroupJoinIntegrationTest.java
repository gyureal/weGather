package com.example.wegather.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.groupJoin.dto.GroupJoinRequestDto;
import com.example.wegather.group.dto.SmallGroupDto;
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

@DisplayName("소모임 가입 통합 테스트")
class SmallGroupJoinIntegrationTest extends IntegrationTest{
  private static final String memberPassword = "1234";
  private MemberDto member01;
  private MemberDto member02;
  private MemberDto member03;
  private SmallGroupDto group01;

  @BeforeEach
  void initData() {
    member01 = insertMember("member01", memberPassword, MemberType.ROLE_USER);
    member02 = insertMember("member02", memberPassword, MemberType.ROLE_USER);
    member03 = insertMember("member03", memberPassword, MemberType.ROLE_USER);
    group01 = insertSmallGroup("group01", 100L, member01);
  }

  @Test
  @DisplayName("소모임 가입 요청에 성공합니다.")
  void smallGroupJoinRequest_success() {
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

  @Test
  @DisplayName("최대 회원수를 초과하여 소모임 가입 요청에 실패합니다.")
  void smallGroupJoinRequest_fail_because_exceed_max_member_count() {
    // given
    SmallGroupDto smallGroup = insertSmallGroup("group01", 1L, member01);
    MemberDto joinMember1 = member02;
    Long newRequestId = requestSmallGroupJoinRequest(smallGroup, joinMember1).as(
        Long.class); // 1명 가입 요청
    requestApproveSmallGroupJoin(smallGroup, newRequestId, member01); // 승인 -> 1명 가입

    MemberDto joinMember2 = member03;
    // when
    ExtractableResponse<Response> response = requestSmallGroupJoinRequest(smallGroup, joinMember2);
    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("소모임 가입 요청 목록을 조회합니다.")
  void readAllJoinRequests_success() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;
    requestSmallGroupJoinRequest(smallGroup, joinMember); // 가입 요청
    int page = 0;

    ExtractableResponse<Response> response = requestReadAllJoinRequests(
        smallGroup, page, member01);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<GroupJoinRequestDto> content = response.jsonPath()
        .getList("content", GroupJoinRequestDto.class);
    GroupJoinRequestDto requestDto = content.get(0);
    assertThat(requestDto.getSmallGroupJoinId()).isEqualTo(group01.getId());
    assertThat(requestDto.getMemberId()).isEqualTo(joinMember.getId());
    assertThat(requestDto.getUsername()).isEqualTo(joinMember.getUsername());
    assertThat(requestDto.getName()).isEqualTo(joinMember.getName());
    assertThat(requestDto.getProfileImage()).isEqualTo(joinMember.getProfileImage());
  }

  @Test
  @DisplayName("소모임장이 아니라서 소모임 가입 요청 목록 조회에 실패합니다.")
  void readAllJoinRequests_fail_because_not_leader() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;
    requestSmallGroupJoinRequest(smallGroup, joinMember); // 가입 요청
    int page = 0;

    ExtractableResponse<Response> response = requestReadAllJoinRequests(
        smallGroup, page, member02);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임 가입 요청을 승인합니다.")
  void approveSmallGroupJoin_success() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;
    Long requestId = requestSmallGroupJoinRequest(smallGroup, joinMember).as(Long.class);// 가입 요청

    ExtractableResponse<Response> response = requestApproveSmallGroupJoin(
        smallGroup, requestId, member01);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("소모임장이 아니어서 소모임 가입 요청을 실패합니다.")
  void approveSmallGroupJoin_fail_because_not_leader() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;
    Long requestId = requestSmallGroupJoinRequest(smallGroup, joinMember).as(Long.class);// 가입 요청

    ExtractableResponse<Response> response = requestApproveSmallGroupJoin(
        smallGroup, requestId, member02);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임 가입 요청을 거절합니다.")
  void rejectSmallGroupJoin_success() {
    SmallGroupDto smallGroup = group01;
    MemberDto joinMember = member02;
    Long requestId = requestSmallGroupJoinRequest(smallGroup, joinMember).as(Long.class);// 가입 요청

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(member01.getUsername(), memberPassword)
        .pathParam("id", smallGroup.getId())
        .pathParam("requestId", requestId)
        .when().post("smallGroups/{id}/join/requests/{requestId}/reject")
        .then().log().all().extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  private ExtractableResponse<Response> requestApproveSmallGroupJoin(SmallGroupDto smallGroup,
      Long requestId, MemberDto loginMember) {
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(loginMember.getUsername(), memberPassword)
        .pathParam("id", smallGroup.getId())
        .pathParam("requestId", requestId)
        .when().post("smallGroups/{id}/join/requests/{requestId}/approve")
        .then().log().all().extract();
    return response;
  }

  private ExtractableResponse<Response> requestReadAllJoinRequests(SmallGroupDto smallGroup,
      int page, MemberDto requester) {
    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(requester.getUsername(), memberPassword)
        .pathParam("id", smallGroup.getId())
        .queryParam("page", page)
        .when().get("smallGroups/{id}/join/requests")
        .then().log().all().extract();
    return response;
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

  private SmallGroupDto insertSmallGroup(String groupName, Long maxMemberCount, MemberDto member) {
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
