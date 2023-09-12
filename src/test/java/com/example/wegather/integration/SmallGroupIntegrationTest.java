package com.example.wegather.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpStatus;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("소모임 통합 테스트")
public class SmallGroupIntegrationTest extends IntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private SmallGroupRepository smallGroupRepository;

  @BeforeEach
  void initRestAssuredApplicationContext() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }

  private static final String memberUsername = "member01";
  private static final String memberPassword = "1234";
  private MemberDto member01;
  private MemberDto member02;
  private MemberDto admin01;
  private SmallGroupDto group01;
  private SmallGroupDto group02;
  private SmallGroupDto group03;

  @BeforeEach
  void initData() {
    member01 = insertMember(memberUsername, memberPassword, MemberType.ROLE_USER);
    member02 = insertMember("member02", "1234", MemberType.ROLE_USER);
    admin01 = insertMember("admin01", "1234", MemberType.ROLE_ADMIN);

    group01 = insertGroup("탁사모", 300L, Arrays.asList("탁구", "친목"));
    group02 = insertGroup("책사모", 100L, Arrays.asList("독서", "친목"));
    group03 = insertGroup("토사모", 200L, Arrays.asList("토익", "스터디"));
  }

  @Test
  @DisplayName("소모임을 생성합니다.")
  void createInterestSuccessfully() {
    List<String> interests = Arrays.asList("볼링");

    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .groupName("볼사모")
        .description("볼링을 사랑하는 사람들의 모임입니다.")
        .streetAddress("서울특별시 중구 세종대로 125")
        .maxMemberCount(300L)
        .build();

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    SmallGroupDto result = response.body().as(SmallGroupDto.class);
    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("id", "leaderId", "leaderUsername")
        .isEqualTo(request);
  }

  @Test
  @DisplayName("id로 소그룹 조회를 성공합니다.")
  void getSmallGroupSuccessfully() {
    Long groupId = group01.getId();

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .pathParam("id", groupId)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups/{id}")
        .then().log().all()
        .extract();

    SmallGroupDto result = response.body().as(SmallGroupDto.class);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(group01);
  }

  @Test
  @DisplayName("그룹 이름과 최대회원수 범위로 소그룹 조회를 성공합니다.")
  void searchSmallGroupByGroupNameAndMaxMemberCountRangeSuccessfully() {
    SmallGroupSearchCondition smallGroupSearchCondition = SmallGroupSearchCondition.builder()
        .smallGroupName("사모")
        .maxMemberCountFrom(100)
        .maxMemberCountTo(201)
        .build();
    int size = 2;
    int page = 0;

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .body(smallGroupSearchCondition)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<SmallGroupDto> result = response.jsonPath().getList("content", SmallGroupDto.class);
    assertThat(result).hasSize(2);
    assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(group02, group03);
    // 페이징 관련 리턴값 검증
    int pageSize = (int) response.path("pageable.pageSize");
    int pageNumber = (int) response.path("pageable.pageNumber");
    assertThat(pageSize).isEqualTo(size);
    assertThat(pageNumber).isEqualTo(page);
  }

  @Test
  @Disabled
  @DisplayName("관심사로 소그룹 조회를 성공합니다.")
  void searchSmallGroupByInterestsSuccessfully() {
    SmallGroupSearchCondition smallGroupSearchCondition = SmallGroupSearchCondition.builder()
        .interest("친목")
        .build();
    int size = 5;
    int page = 0;

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .body(smallGroupSearchCondition)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups")
        .then().log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<SmallGroupDto> result = response.jsonPath().getList("content", SmallGroupDto.class);
    assertThat(result).hasSize(2);
    assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(group01, group02);
    // 페이징 관련 리턴값 검증
    int pageSize = (int) response.path("pageable.pageSize");
    int pageNumber = (int) response.path("pageable.pageNumber");
    assertThat(pageSize).isEqualTo(size);
    assertThat(pageNumber).isEqualTo(page);
  }

  @Test
  @DisplayName("소모임 정보를 수정합니다.")
  void updateSmallGroupSuccessfully() {
    UpdateSmallGroupRequest request = UpdateSmallGroupRequest.builder()
        .groupName("수정 모임")
        .description("수정하였습니다.")
        .streetAddress("수정광역시 수정로")
        .maxMemberCount(123L)
        .build();

    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .pathParam("id", group01.getId())
        .body(request)
        .contentType(ContentType.JSON)
        .when().put("/smallGroups/{id}")
        .then().log().ifValidationFails()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup updated = smallGroupRepository.findById(group01.getId())
        .orElseThrow(() -> new AssertionFailure("group ID를 찾을 수 없습니다."));
    assertThat(updated.getName()).isEqualTo(request.getGroupName());
    assertThat(updated.getDescription()).isEqualTo(request.getDescription());
    assertThat(updated.getAddress().getStreetAddress()).isEqualTo(request.getStreetAddress());
    assertThat(updated.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount());
  }
  @Test
  @DisplayName("소모임장이 아니어서 소모임 수정에 실패합니다.")
  void updateSmallGroupFailBecauseOfNotLeader() {
    UpdateSmallGroupRequest request = UpdateSmallGroupRequest.builder()
        .groupName("수정 모임")
        .description("수정하였습니다.")
        .streetAddress("수정광역시 수정로")
        .maxMemberCount(123L)
        .build();

    RestAssured
        .given().log().all()
        .auth().basic("member02", "1234")
        .pathParam("id", group01.getId())
        .body(request)
        .contentType(ContentType.JSON)
        .when().put("/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("소모임 삭제에 성공합니다.")
  void deleteSmallGroupSuccessfully() {
    Long id = group01.getId();

    RestAssured
        .given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .pathParam("id", id)
        .contentType(ContentType.JSON)
        .when().delete("/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("소모임장이 아니어서 소모임 삭제에 실패합니다.")
  void deleteSmallGroupFailBecauseOfNotLeader() {
    Long id = group01.getId();

    RestAssured
        .given().log().all()
        .auth().basic("member02", "1234")
        .pathParam("id", id)
        .contentType(ContentType.JSON)
        .when().delete("/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("소모임에 관심사를 추가합니다.")
  void addInterestToSmallGroup() {
    // given
    InterestDto interestDto = insertInterest("축구");

    // when
    ExtractableResponse<Response> response = requestAddInterest(group01.getId(),
        interestDto.getId(), member01);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("소모임장이 아니라서 소모임 관심사 추가에 실패합니다.")
  void addInterestToSmallGroup_fail_because_not_leader() {
    // given
    InterestDto interestDto = insertInterest("축구");

    // when
    ExtractableResponse<Response> response = requestAddInterest(group01.getId(),
        interestDto.getId(), member02);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("소모임에 관심사를 삭제합니다.")
  void removeInterestToSmallGroup() {
    // given
    InterestDto interestDto = insertInterest("축구");
    requestAddInterest(group01.getId(), interestDto.getId(), member01);

    // when
    ExtractableResponse<Response> response = requestRemoveInterest(group01.getId(),
        interestDto.getId(), member01);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("소모임 장이 아니라서 소모임에 관심사 삭제에 실패합니다.")
  void removeInterestToSmallGroup_fail_because_not_leader() {
    // given
    InterestDto interestDto = insertInterest("축구");
    requestAddInterest(group01.getId(), interestDto.getId(), member01);

    // when
    ExtractableResponse<Response> response = requestRemoveInterest(group01.getId(),
        interestDto.getId(), member02);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  private SmallGroupDto insertGroup(String groupName, Long maxMemberCount, List<String> interests) {
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .groupName(groupName)
        .description("테스트입니다.")
        .streetAddress("서울특별시 중구 세종대로 125")
        .maxMemberCount(maxMemberCount)
        .build();

    return RestAssured.given().log().all()
        .auth().basic(memberUsername, memberPassword)
        .body(request)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups")
        .then().log().ifStatusCodeIsEqualTo(HttpStatus.SC_CREATED)
        .extract().as(SmallGroupDto.class);
  }

  private MemberDto insertMember(String username, String password, MemberType memberType) {
    SignUpRequest request = SignUpRequest.builder()
        .username(username)
        .password(password)
        .name("testUser")
        .phoneNumber("010-1234-1234")
        .memberType(memberType)
        .build();

    return RestAssured.given().body(request).contentType(ContentType.JSON)
        .when().post("/sign-up")
        .then().extract().as(MemberDto.class);
  }

  private InterestDto insertInterest(String interestName) {
    CreateInterestRequest request = CreateInterestRequest.builder()
        .interestName("축구")
        .build();

    return RestAssured.given().body(request).contentType(ContentType.JSON)
        .auth().basic(memberUsername, memberPassword)
        .when().post("/interests")
        .then().extract().as(InterestDto.class);
  }

  private ExtractableResponse<Response> requestAddInterest(Long smallGroupId, Long interestId, MemberDto loginMember) {
    return RestAssured.given().log().all()
        .auth().basic(loginMember.getUsername(), memberPassword)
        .pathParam("id", smallGroupId)
        .queryParam("interestId", interestId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/interest")
        .then().log().all()
        .extract();
  }

  private ExtractableResponse<Response> requestRemoveInterest(Long smallGroupId, Long interestId, MemberDto loginMember) {
    return RestAssured.given().log().all()
        .auth().basic(loginMember.getUsername(), memberPassword)
        .pathParam("id", smallGroupId)
        .queryParam("interestId", interestId)
        .contentType(ContentType.JSON)
        .when().delete("/smallGroups/{id}/interest")
        .then().log().all()
        .extract();
  }
}
