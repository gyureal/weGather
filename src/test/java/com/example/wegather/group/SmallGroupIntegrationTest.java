package com.example.wegather.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.wegather.auth.AuthControllerTest;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.IntegrationTest;
import com.example.wegather.interest.InterestIntegrationTest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.apache.http.HttpStatus;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("소모임 통합 테스트")
class SmallGroupIntegrationTest extends IntegrationTest {

  @Autowired
  private SmallGroupRepository smallGroupRepository;

  private static final String memberUsername = "member01";
  private static final String memberPassword = "1234";
  private MemberDto member01;
  private MemberDto member02;
  private SmallGroupDto group01;
  private SmallGroupDto group02;
  private SmallGroupDto group03;

  @BeforeEach
  void initData() {
    member01 = insertMember(memberUsername, "testUser1@gmail.com", memberPassword);
    member02 = insertMember("member02", "testUser2@gmail.com", "1234");
    group01 = createGroupForTest("taksamo","탁사모", 300L, member01.getUsername());
    group02 = createGroupForTest("chacksamo","책사모", 100L,member01.getUsername());
    group03 = createGroupForTest("tosamo","토사모", 200L, member01.getUsername());
  }

  @Test
  @DisplayName("소모임을 생성합니다.")
  void createInterestSuccessfully() {
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .path("ballsamo")
        .name("볼사모")
        .shortDescription("볼링을 사랑하는 사람들의 모임입니다.")
        .fullDescription("<h2>환영합니다</h2>")
        .maxMemberCount(30L)
        .build();

    ExtractableResponse<Response> response = requestCreateGroup(request, member01.getUsername());

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    SmallGroupDto result = response.body().as(SmallGroupDto.class);
    assertThat(result.getPath()).isEqualTo(request.getPath());
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getShortDescription()).isEqualTo(request.getShortDescription());
    assertThat(result.getFullDescription()).isEqualTo(request.getFullDescription());
  }

  @Test
  @DisplayName("소모임 생성 시, path가 중복되어 생성에 실패합니다.")
  void createSmallGroupFail_path_duplicated() {
    // given
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .path("ballsamo")
        .name("볼사모").build();
    // 생성
    requestCreateGroup(request, member01.getUsername());

    // when
    ExtractableResponse<Response> response = requestCreateGroup(request, member01.getUsername());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("path 로 소그룹 조회를 성공합니다.")
  void getSmallGroupSuccessfully() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    String path = group01.getPath();

    ExtractableResponse<Response> response = RestAssured.given().log().all().spec(spec)
        .pathParam("path", path)
        .contentType(ContentType.JSON)
        .when().get("/smallGroups/{path}")
        .then().log().all()
        .extract();

    SmallGroupDto result = response.body().as(SmallGroupDto.class);

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(group01);
    assertThat(result.isJoinable()).isFalse();
  }

  @Test
  @DisplayName("그룹 이름과 최대회원수 범위로 소그룹 조회를 성공합니다.")
  @Disabled
  void searchSmallGroupByGroupNameAndMaxMemberCountRangeSuccessfully() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    SmallGroupSearchCondition smallGroupSearchCondition = SmallGroupSearchCondition.builder()
        .smallGroupName("사모")
        .maxMemberCountFrom(100)
        .maxMemberCountTo(201)
        .build();
    int size = 2;
    int page = 0;

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .spec(spec)
        .body(smallGroupSearchCondition).contentType(ContentType.JSON)
        .queryParam("size", size, "page", page)
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
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    SmallGroupSearchCondition smallGroupSearchCondition = SmallGroupSearchCondition.builder()
        .interest("친목")
        .build();
    int size = 5;
    int page = 0;

    ExtractableResponse<Response> response = RestAssured.given().log().all()
        .spec(spec)
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
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    UpdateSmallGroupRequest request = UpdateSmallGroupRequest.builder()
        .groupName("수정 모임")
        .shortDescription("수정하였습니다.")
        .streetAddress("수정광역시 수정로")
        .maxMemberCount(123L)
        .build();

    ExtractableResponse<Response> response = RestAssured
        .given().log().all()
        .spec(spec)
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
    assertThat(updated.getShortDescription()).isEqualTo(request.getShortDescription());
    assertThat(updated.getAddress().getStreetAddress()).isEqualTo(request.getStreetAddress());
    assertThat(updated.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount());
  }
  @Test
  @DisplayName("소모임장이 아니어서 소모임 수정에 실패합니다.")
  void updateSmallGroupFailBecauseOfNotLeader() {
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    UpdateSmallGroupRequest request = UpdateSmallGroupRequest.builder()
        .groupName("수정 모임")
        .shortDescription("수정하였습니다.")
        .streetAddress("수정광역시 수정로")
        .maxMemberCount(123L)
        .build();

    RestAssured
        .given().log().all()
        .spec(spec)
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
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    Long id = group01.getId();

    RestAssured
        .given().log().all()
        .spec(spec)
        .pathParam("id", id)
        .contentType(ContentType.JSON)
        .when().delete("/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("소모임장이 아니어서 소모임 삭제에 실패합니다.")
  void deleteSmallGroupFailBecauseOfNotLeader() {
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    Long id = group01.getId();

    RestAssured
        .given().log().all()
        .spec(spec)
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
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());

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
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());

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
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());
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
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());
    requestAddInterest(group01.getId(), interestDto.getId(), member01);

    // when
    ExtractableResponse<Response> response = requestRemoveInterest(group01.getId(),
        interestDto.getId(), member02);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  private ExtractableResponse<Response> requestCreateGroup(CreateSmallGroupRequest request, String requesterId) {
    RequestSpecification spec = AuthControllerTest.signIn(requesterId, memberPassword);

    return RestAssured.given().log().all().spec(spec)
        .body(request).contentType(ContentType.JSON)
        .when().post("/smallGroups")
        .then().log().ifStatusCodeIsEqualTo(HttpStatus.SC_CREATED)
        .extract();
  }

  private SmallGroupDto createGroupForTest(String path, String groupName, Long maxMemberCount, String requestId) {
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .path(path)
        .name(groupName)
        .shortDescription("볼링을 사랑하는 사람들의 모임입니다.")
        .fullDescription("fullDescription")
        .maxMemberCount(maxMemberCount)
        .build();

    return requestCreateGroup(request, requestId).as(SmallGroupDto.class);
  }

  private MemberDto insertMember(String username, String email ,String password) {
    SignUpRequest request = SignUpRequest.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();

    return AuthControllerTest.signUp(request).as(MemberDto.class);
  }

  private ExtractableResponse<Response> requestAddInterest(Long smallGroupId, Long interestId, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);

    return RestAssured.given().log().all()
        .spec(spec)
        .pathParam("id", smallGroupId)
        .queryParam("interestId", interestId)
        .contentType(ContentType.JSON)
        .when().post("/smallGroups/{id}/interest")
        .then().log().all()
        .extract();
  }

  private ExtractableResponse<Response> requestRemoveInterest(Long smallGroupId, Long interestId, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);

    return RestAssured.given().log().all()
        .spec(spec)
        .pathParam("id", smallGroupId)
        .queryParam("interestId", interestId)
        .contentType(ContentType.JSON)
        .when().delete("/smallGroups/{id}/interest")
        .then().log().all()
        .extract();
  }
}
