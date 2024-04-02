package com.example.wegather.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.example.wegather.auth.AuthControllerTest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupInterest;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.domain.vo.RecruitingType;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.ManagerAndMemberDto;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.SmallGroupSearchDto;
import com.example.wegather.group.dto.UpdateBannerRequest;
import com.example.wegather.group.dto.UpdateGroupDescriptionRequest;
import com.example.wegather.IntegrationTest;
import com.example.wegather.group.dto.UpdateGroupWithMultipartImageRequest;
import com.example.wegather.group.domain.entity.SmallGroupMember;
import com.example.wegather.group.domain.repotitory.SmallGroupMemberRepository;
import com.example.wegather.group.domain.vo.SmallGroupMemberType;
import com.example.wegather.interest.InterestIntegrationTest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.MemberDto;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("소모임 통합 테스트")
public class SmallGroupIntegrationTest extends IntegrationTest {

  @Autowired
  private SmallGroupRepository smallGroupRepository;
  @Autowired
  private SmallGroupMemberRepository smallGroupMemberRepository;
  @Autowired
  private MemberRepository memberRepository;
  @MockBean
  StoreFile storeFile;

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
    // given
    CreateSmallGroupRequest request = CreateSmallGroupRequest.builder()
        .path("ballsamo")
        .name("볼사모")
        .shortDescription("볼링을 사랑하는 사람들의 모임입니다.")
        .fullDescription("<h2>환영합니다</h2>")
        .maxMemberCount(30L)
        .build();

    // when
    ExtractableResponse<Response> response = requestCreateGroup(request, member01.getUsername());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    SmallGroupDto result = response.body().as(SmallGroupDto.class);
    assertThat(result.getPath()).isEqualTo(request.getPath());
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getShortDescription()).isEqualTo(request.getShortDescription());
    assertThat(result.getFullDescription()).isEqualTo(request.getFullDescription());
    // 소모임 생성한 사람이 소모임의 관리자로 저장되어 있는지 확인합니다.
    SmallGroupMember smallGroupMember = smallGroupMemberRepository.findBySmallGroup_IdAndMember_Id(
            result.getId(), member01.getId())
        .orElseThrow(() -> new RuntimeException("소모임 회원을 찾을 수 없습니다."));
    assertThat(smallGroupMember.getSmallGroupMemberType()).isEqualTo(SmallGroupMemberType.MANAGER);
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

    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .pathParam("path", path)
        .contentType(ContentType.JSON)
        .when().get("/api/smallGroups/{path}")
        .then().log().ifValidationFails()
        .extract();

    SmallGroupDto result = response.body().as(SmallGroupDto.class);

    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("joinable", "managerOrMember")
        .isEqualTo(group01);
    assertThat(result.isJoinable()).isFalse();
    assertThat(result.isManagerOrMember()).isTrue();
  }

  @Test
  @DisplayName("소모임의 관리자와 회원 목록을 조회합니다.")
  void readGroupManagersAndMembersSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    String path = group01.getPath();

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .pathParam("path", path)
        .when().get("/api/smallGroups/{path}/managers-and-members")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

    JsonPath jsonPath = response.body().jsonPath();

    List<ManagerAndMemberDto> managerAndMemberDtos = response.body().jsonPath().getList(".",ManagerAndMemberDto.class);
    assertThat(managerAndMemberDtos).hasSize(1);
    // 리더는 자동으로 관리자로 추가된다.
    Member leader = memberRepository.findById(group01.getLeaderId())
        .orElseThrow(() -> new RuntimeException("test failed"));
    assertThat(managerAndMemberDtos.get(0).getName()).isEqualTo(leader.getUsername());
    assertThat(managerAndMemberDtos.get(0).getIntroduction()).isEqualTo(leader.getIntroductionText());
    assertThat(managerAndMemberDtos.get(0).getImage()).isEqualTo(leader.getProfileImage());
    assertThat(managerAndMemberDtos.get(0).isManager()).isTrue();
  }

  @Test
  @DisplayName("base64 형식의 이미지로 소모임의 배너 이미지를 수정합니다.")
  void updateSmallGroupBannerByBase64ImageSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    String path = group01.getPath();
    UpdateBannerRequest request = UpdateBannerRequest.builder()
        .image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIA")
        .originalImageName("test.jpg")
        .build();
    // 이미지 저장 mock
    String storeFileName = "storeFileName";
    given(storeFile.storeFile(any(), any())).willReturn(UploadFile.of("", storeFileName));

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .pathParam("path", path)
        .contentType(ContentType.JSON)
        .body(request)
        .when().post("/api/smallGroups/{path}/banner")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    // 호출 검증지
    then(storeFile).should().storeFile(any(), any());

    SmallGroup smallGroup = smallGroupRepository.findByPath(path)
        .orElseThrow(() -> new RuntimeException("test fail"));
    assertThat(smallGroup.getBanner()).isEqualTo(storeFileName);
  }

  @Test
  @DisplayName("MultipartFile 형식의 이미지로 소모임의 배너 이미지를 수정합니다.")
  void updateSmallGroupBannerByMultipartFileSuccessfully() {
    // given
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    String path = group01.getPath();
    // 가짜 파일
    String fakeFileContent = "fakeFile";
    byte[] fakeFileBytes = fakeFileContent.getBytes();
    // 이미지 저장 mock
    String storeFileName = "storeFileName";
    given(storeFile.storeFile(any())).willReturn(UploadFile.of("", storeFileName));

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails().spec(spec)
        .pathParam("path", path)
        .contentType(ContentType.MULTIPART)
        .multiPart("file", "fake-file.txt", fakeFileBytes)
        .when().post("/api/smallGroups/{path}/banner/v2")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    // 호출 검증지
    then(storeFile).should().storeFile(any());

    SmallGroup smallGroup = smallGroupRepository.findByPath(path)
        .orElseThrow(() -> new RuntimeException("test fail"));
    assertThat(smallGroup.getBanner()).isEqualTo(storeFileName);
  }

  @Test
  @DisplayName("소모임 명으로 소모임 검색에 성공합니다.")
  void searchSmallGroupByGroupNameSuccessfully() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    String title = "사모";
    int size = 2;
    int page = 0;

    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .queryParam("title", title)
        .queryParam("size", size)
        .queryParam("page", page)
        .when().get("/api/smallGroups")
        .then().log().ifValidationFails()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    List<SmallGroupSearchDto> result = response.jsonPath().getList("content", SmallGroupSearchDto.class);
    assertThat(result).hasSize(2);
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

    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .body(smallGroupSearchCondition)
        .queryParam("size", size, "page", page)
        .contentType(ContentType.JSON)
        .when().get("/api/smallGroups")
        .then().log().ifValidationFails()
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
  @DisplayName("소모임 소개 정보를 수정합니다.")
  void updateSmallGroupSuccessfully() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    UpdateGroupDescriptionRequest request = UpdateGroupDescriptionRequest.builder()
        .shortDescription("수정하였습니다")
        .fullDescription("수정하였습니다")
        .build();

    ExtractableResponse<Response> response = RestAssured
        .given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", group01.getPath())
        .body(request)
        .contentType(ContentType.JSON)
        .when().put("/api/smallGroups/{path}")
        .then().log().ifValidationFails()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup updated = smallGroupRepository.findById(group01.getId())
        .orElseThrow(() -> new AssertionFailure("group ID를 찾을 수 없습니다."));
    assertThat(updated.getShortDescription()).isEqualTo(request.getShortDescription());
    assertThat(updated.getFullDescription()).isEqualTo(request.getFullDescription());
  }

  @Test
  @DisplayName("소모임 소개 정보 수정 시, image 입력값이 있을 경우, 이미지 업로드 또한 수행합니다.")
  void updateSmallGroupSuccessfullyInCaseImageExists() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    UpdateGroupDescriptionRequest request = UpdateGroupDescriptionRequest.builder()
        .shortDescription("수정하였습니다")
        .fullDescription("수정하였습니다")
        .image("data:image/png;base64, new image")
        .originalImageName("image.jpg")
        .build();
    String storeFileName = "new Image";
    given(storeFile.decodeBase64Image(any())).willReturn(new byte[1]);
    given(storeFile.storeFile(any(), any())).willReturn(new UploadFile("", storeFileName));

    ExtractableResponse<Response> response = RestAssured
        .given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", group01.getPath())
        .body(request)
        .contentType(ContentType.JSON)
        .when().put("/api/smallGroups/{path}")
        .then().log().ifValidationFails()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup updated = smallGroupRepository.findById(group01.getId())
        .orElseThrow(() -> new AssertionFailure("group ID를 찾을 수 없습니다."));
    assertThat(updated.getShortDescription()).isEqualTo(request.getShortDescription());
    assertThat(updated.getFullDescription()).isEqualTo(request.getFullDescription());
    // 이미지 업로드 메서드 호출 테스트
    then(storeFile).should().storeFile(any(), any());
    // 이미지 DB 저장 테스트
    assertThat(updated.getImage()).isEqualTo(storeFileName);
  }

  @Test
  @DisplayName("소모임 관리자가 아니어서 소모임 소개 수정에 실패합니다.")
  void updateSmallGroupFailBecauseOfNotManager() {
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    UpdateGroupDescriptionRequest request = UpdateGroupDescriptionRequest.builder()
        .shortDescription("수정하였습니다")
        .fullDescription("수정하였습니다")
        .build();

    RestAssured
        .given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", group01.getPath())
        .body(request)
        .contentType(ContentType.JSON)
        .when().put("/api/smallGroups/{path}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임 소개 정보 수정 시 (MultipartFile 이미지 입력값), image 입력값이 있을 경우, 이미지 업로드 또한 수행합니다.")
  void updateGroupWithMultipartImageSuccessfullyInCaseImageExists() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    UpdateGroupWithMultipartImageRequest descriptionInfo = UpdateGroupWithMultipartImageRequest.builder()
        .shortDescription("수정하였습니다")
        .fullDescription("수정하였습니다")
        .build();
    // 가짜 파일
    String fakeFileContent = "fakeFile";
    byte[] fakeFileBytes = fakeFileContent.getBytes();
    // 이미지 저장 mock
    String storeFileName = "storeFileName";
    given(storeFile.storeFile(any())).willReturn(UploadFile.of("", storeFileName));

    ExtractableResponse<Response> response = RestAssured
        .given().log().ifValidationFails()
          .spec(spec)
          .pathParam("path", group01.getPath())
          .contentType(ContentType.MULTIPART)
          //.multiPart("descriptionInfo", descriptionInfo, APPLICATION_JSON_VALUE)  // charset 미지정으로 한글 깨짐
        .multiPart(
            new MultiPartSpecBuilder(descriptionInfo)
                .controlName("descriptionInfo")
                .mimeType(APPLICATION_JSON_VALUE)
                .charset(StandardCharsets.UTF_8)
                .build()
        )
          .multiPart("image", "fake-file.txt", fakeFileBytes, MULTIPART_FORM_DATA_VALUE)
        .when()
          .put("/api/smallGroups/{path}/v2")
        .then()
          .log().ifValidationFails()
          .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup updated = smallGroupRepository.findById(group01.getId())
        .orElseThrow(() -> new AssertionFailure("group ID를 찾을 수 없습니다."));
    assertThat(updated.getShortDescription()).isEqualTo(descriptionInfo.getShortDescription());
    assertThat(updated.getFullDescription()).isEqualTo(descriptionInfo.getFullDescription());
    // 이미지 업로드 메서드 호출 테스트
    then(storeFile).should().storeFile(any());
    // 이미지 DB 저장 테스트
    assertThat(updated.getImage()).isEqualTo(storeFileName);
  }

  @Test
  @DisplayName("소모임 삭제에 성공합니다.")
  void deleteSmallGroupSuccessfully() {
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);
    Long id = group01.getId();

    RestAssured
        .given().log().ifValidationFails()
        .spec(spec)
        .pathParam("id", id)
        .contentType(ContentType.JSON)
        .when().delete("/api/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("소모임 관리자가 아니어서 소모임 삭제에 실패합니다.")
  void deleteSmallGroupFailBecauseOfNotManager() {
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    Long id = group01.getId();

    RestAssured
        .given().log().ifValidationFails()
        .spec(spec)
        .pathParam("id", id)
        .contentType(ContentType.JSON)
        .when().delete("/api/smallGroups/{id}")
        .then().log().ifValidationFails()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임에 관심사를 추가합니다.")
  void addInterestToSmallGroup() {
    // given
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());

    // when
    ExtractableResponse<Response> response = requestAddInterest(group01.getPath(),
        interestDto.getName(), member01);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

    SmallGroup smallGroup = findSmallWithInterestGroupByPath(group01.getPath());
    Set<SmallGroupInterest> smallGroupInterests = smallGroup.getSmallGroupInterests();
    assertThat(smallGroupInterests).hasSize(1);
  }

  @Test
  @DisplayName("소모임 관리자가 아니라서 소모임 관심사 추가에 실패합니다.")
  void addInterestToSmallGroup_fail_because_not_manager() {
    // given
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());

    // when
    ExtractableResponse<Response> response = requestAddInterest(group01.getPath(),
        interestDto.getName(), member02);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임에 관심사를 삭제합니다.")
  void removeInterestToSmallGroup() {
    // given
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());
    requestAddInterest(group01.getPath(), interestDto.getName(), member01);

    // when
    ExtractableResponse<Response> response = requestRemoveInterest(group01.getPath(),
        interestDto.getName(), member01);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

    SmallGroup smallGroup = findSmallWithInterestGroupByPath(group01.getPath());
    Set<SmallGroupInterest> smallGroupInterests = smallGroup.getSmallGroupInterests();
    assertThat(smallGroupInterests).isEmpty();
  }

  SmallGroup findSmallWithInterestGroupByPath(String path) {
    return smallGroupRepository.findWithInterestByPath(path)
        .orElseThrow(() -> new RuntimeException("test 실패"));
  }

  @Test
  @DisplayName("소모임 관리자가 아니라서 소모임에 관심사 삭제에 실패합니다.")
  void removeInterestToSmallGroup_fail_because_not_manager() {
    // given
    InterestDto interestDto = InterestIntegrationTest.insertInterest("축구", member01.getUsername());
    requestAddInterest(group01.getPath(), interestDto.getName(), member01);

    // when
    ExtractableResponse<Response> response = requestRemoveInterest(group01.getPath(),
        interestDto.getName(), member02);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  @DisplayName("소모임의 관심사를 조회합니다.")
  void getSmallGroupInterestsByPath_success() {
    // given
    InterestDto interest1 = InterestIntegrationTest.insertInterest("축구", member01.getUsername());
    InterestDto interest2 = InterestIntegrationTest.insertInterest("농구", member01.getUsername());
    requestAddInterest(group01.getPath(), interest1.getName(), member01);
    requestAddInterest(group01.getPath(), interest2.getName(), member01);
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    // when
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", group01.getPath())
        .when().get("/api/smallGroups/{path}/interests")
        .then().log().ifValidationFails()
        .extract();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

    List<String> interests = response.body().jsonPath().getList(".",String.class);
    assertThat(interests).hasSize(2);
    assertThat(interests.get(0)).isEqualTo("축구");
    assertThat(interests.get(1)).isEqualTo("농구");
  }

  @Test
  @DisplayName("소모임 공개를 성공합니다.")
  void smallGroupPublishSuccessfully() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestSmallGroupPublish(
        spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup smallGroup = findSmallGroupById(path);
    assertThat(smallGroup.isPublished()).isTrue();
    assertThat(smallGroup.getPublishedDateTime()).isNotNull();
  }

  @Test
  @DisplayName("관리자가 아니어서 소모임 공개에 실패합니다.")
  void smallGroupPublishFail_not_manager() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestSmallGroupPublish(
        spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  private ExtractableResponse<Response> requestSmallGroupPublish(RequestSpecification spec) {
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", group01.getPath())
        .when().post("/api/smallGroups/{path}/publish")
        .then().log().ifValidationFails()
        .extract();
    return response;
  }

  @Test
  @DisplayName("소모임 인원 모집 오픈에 성공합니다.")
  void openRecruitingSuccessfully() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestOpenRecruiting(path, spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup smallGroup = findSmallGroupById(path);
    assertThat(smallGroup.isRecruiting()).isTrue();
    assertThat(smallGroup.getRecruitingType()).isEqualTo(RecruitingType.FCFS);
    assertThat(smallGroup.getRecruitingUpdatedDateTime()).isNotNull();
  }

  @Test
  @DisplayName("관리자가 아니어서 소모임 인원 모집 오픈에 실패합니다.")
  void openRecruitingFail_not_manger() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestOpenRecruiting(path, spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  private ExtractableResponse<Response> requestOpenRecruiting(String path, RequestSpecification spec) {
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", path)
        .queryParam("recruitingType", RecruitingType.FCFS)
        .when().post("/api/smallGroups/{path}/open-recruiting")
        .then().log().ifValidationFails()
        .extract();
    return response;
  }

  @Test
  @DisplayName("소모임 종료에 성공합니다.")
  void closeSmallGroupSuccessfully() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member01.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestCloseSmallGroup(path ,spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    SmallGroup smallGroup = findSmallGroupById(path);
    assertThat(smallGroup.isClosed()).isTrue();
    assertThat(smallGroup.getClosedDateTime()).isNotNull();
  }

  @Test
  @DisplayName("관리자가 아니어서 소모임 종료에 실패합니다.")
  void closeSmallGroupFail_not_manager() {
    String path = group01.getPath();
    RequestSpecification spec = AuthControllerTest.signIn(member02.getUsername(), memberPassword);

    ExtractableResponse<Response> response = requestCloseSmallGroup(path ,spec);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
  }

  private ExtractableResponse<Response> requestCloseSmallGroup(String path, RequestSpecification spec) {
    ExtractableResponse<Response> response = RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", path)
        .when().post("/api/smallGroups/{path}/close")
        .then().log().ifValidationFails()
        .extract();
    return response;
  }

  public static ExtractableResponse<Response> requestCreateGroup(CreateSmallGroupRequest request, String requesterId) {
    RequestSpecification spec = AuthControllerTest.signIn(requesterId, memberPassword);

    return RestAssured.given().log().ifValidationFails().spec(spec)
        .body(request).contentType(ContentType.JSON)
        .when().post("/api/smallGroups")
        .then().log().ifValidationFails()
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

  private ExtractableResponse<Response> requestAddInterest(String smallGroupPath, String interestName, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);

    return RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", smallGroupPath)
        .queryParam("interestName", interestName)
        .contentType(ContentType.JSON)
        .when().post("/api/smallGroups/{path}/interest")
        .then().log().ifValidationFails()
        .extract();
  }

  private ExtractableResponse<Response> requestRemoveInterest(String smallGroupPath, String interestName, MemberDto loginMember) {
    RequestSpecification spec = AuthControllerTest.signIn(loginMember.getUsername(), memberPassword);

    return RestAssured.given().log().ifValidationFails()
        .spec(spec)
        .pathParam("path", smallGroupPath)
        .queryParam("interestName", interestName)
        .contentType(ContentType.JSON)
        .when().delete("/api/smallGroups/{path}/interest")
        .then().log().ifValidationFails()
        .extract();
  }

  private SmallGroup findSmallGroupById(String path) {
    return smallGroupRepository.findByPath(path)
        .orElseThrow(() -> new RuntimeException("소모임을 찾을 수 없습니다."));
  }
}
