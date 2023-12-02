package com.example.wegather.group.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.group.domain.service.SmallGroupService;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.ManagerAndMemberDto;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchDto;
import com.example.wegather.group.dto.UpdateBannerRequest;
import com.example.wegather.group.dto.UpdateGroupDescriptionRequest;
import com.example.wegather.group.dto.UpdateGroupWithMultipartImageRequest;
import com.example.wegather.group.validator.CreateSmallGroupValidator;
import com.example.wegather.interest.dto.InterestDto;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RequestMapping("/smallGroups")
@RestController
public class SmallGroupController {
  private final SmallGroupService smallGroupService;

  private final CreateSmallGroupValidator createSmallGroupValidator;

  @InitBinder("createSmallGroupRequest")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(createSmallGroupValidator);
  }


  /**
   * 소모임을 생성합니다.
   * @param createSmallGroupRequest 소모임 생성 정보 dto
   * @param memberDetails 로그인한 회원 정보
   * @return 생성된 소모임
   */
  @PostMapping
  public ResponseEntity<SmallGroupDto> createGroup(
      @Valid @RequestBody CreateSmallGroupRequest createSmallGroupRequest,
      @AuthenticationPrincipal MemberDetails memberDetails) {
    SmallGroupDto smallGroupDto = SmallGroupDto.from(
        smallGroupService.addSmallGroup(createSmallGroupRequest, memberDetails.getId()));
    return ResponseEntity.created(URI.create("/smallGroups/" + smallGroupDto.getId()))
        .body(smallGroupDto);
  }

  /**
   * path 로 소모임을 조회합니다.
   * @param path 소모임 경로
   * @return
   */
  @GetMapping("/{path}")
  public ResponseEntity<SmallGroupDto> readGroup(@PathVariable String path,
      @AuthenticationPrincipal MemberDetails memberDetails) {
    return ResponseEntity.ok(smallGroupService.getSmallGroupByPath(path, memberDetails));
  }

  /**
   * 소모임의 관리자와 회원 목록을 같이 조회합니다.
   * @param path 소모임 경로
   * @return
   */
  @GetMapping("/{path}/managers-and-members")
  public ResponseEntity<List<ManagerAndMemberDto>> readGroupManagersAndMembers(@PathVariable String path) {
    return ResponseEntity.ok(smallGroupService.getSmallGroupManagersAndMembers(path));
  }

  /**
   * 소모임 배너 수정 (base64 이미지 형식)
   * 소모임의 관리자만 수정 가능합니다.
   * @return
   */
  @PostMapping("/{path}/banner")
  public ResponseEntity<Void> updateSmallGroupBanner(@AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable String path, @RequestBody @Valid UpdateBannerRequest request) {
    smallGroupService.updateBanner(memberDetails, path ,request);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임 배너 수정 (MultipartFile 이미지 형식)
   * 소모임의 관리자만 수정 가능합니다.
   * @return
   */
  @PostMapping("/{path}/banner/v2")
  public ResponseEntity<Void> updateSmallGroupBanner(@AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable String path, MultipartFile multipartImage) {
    smallGroupService.updateBanner(memberDetails, path ,multipartImage);
    return ResponseEntity.ok().build();
  }

  /**
   * 배너 사용 여부를 변겅합니다.
   * 관리자만 변경가능합니다.
   * @param path
   * @return
   */
  @PostMapping("/{path}/toggle-use-banner")
  public ResponseEntity<Void> toggleUseBanner(@AuthenticationPrincipal MemberDetails memberDetails, @PathVariable String path) {
    smallGroupService.toggleUseBanner(memberDetails, path);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임을 검색합니다.
   * @param keyword 검색어
   * @param pageable 페이징 정보
   * @return 페이징 정보를 포함한 소모임 검색 결과
   */
  @GetMapping
  public ResponseEntity<Page<SmallGroupSearchDto>> searchGroups(
      @RequestParam @Nullable String keyword,
      @PageableDefault(size = 9, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(smallGroupService.searchSmallGroups(keyword, pageable));
  }

  /**
   * 소모임 정보를 업데이트 합니다. (base64 이미지)
   * @param path 소모임 path
   * @param request 소모임 update dto
   * @return
   */
  @PutMapping("/{path}")
  public ResponseEntity<Void> updateGroupDescription(
      @AuthenticationPrincipal MemberDetails principal,
      @PathVariable String path,
      @RequestBody UpdateGroupDescriptionRequest request) {

    smallGroupService.editSmallGroupDescription(principal, path, request);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임 정보를 업데이트 합니다. (MultipartFile 이미지)
   * @param path 소모임 path
   * @param descriptionInfo 소모임 정보
   * @param image 소모임 썸네일 이미지 (MultipartFile 형식)
   * @return
   */
  @PutMapping("/{path}/v2")
  public ResponseEntity<Void> updateGroupWithMultipartImage(
      @AuthenticationPrincipal MemberDetails principal,
      @PathVariable String path,
      @RequestPart("descriptionInfo") UpdateGroupWithMultipartImageRequest descriptionInfo,
      @RequestPart("image") @Nullable MultipartFile image) {

    smallGroupService.editSmallGroupDescriptionWithMultipart(principal, path, descriptionInfo, image);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임을 삭제합니다.
   * @param id 소모임 id
   * @return
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroup(@AuthenticationPrincipal MemberDetails principal, @PathVariable Long id) {
    smallGroupService.deleteSmallGroup(principal, id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 소모임에 관심사를 추가합니다.
   * @param path
   * @param interestName
   * @return
   */
  @PostMapping("/{path}/interest")
  public ResponseEntity<Void> addInterest(
      @AuthenticationPrincipal MemberDetails principal,
      @PathVariable String path, @RequestParam String interestName) {
    smallGroupService.addSmallGroupInterest(principal, path, interestName);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{path}/interests")
  public ResponseEntity<List<String>> getInterests(@PathVariable String path) {
    return ResponseEntity.ok(smallGroupService.getInterests(path));
  }

  /**
   * 소모임에 관심사를 삭제합니다.
   * @param path
   * @param interestName
   * @return
   */
  @DeleteMapping("/{path}/interest")
  public ResponseEntity<List<InterestDto>> removeInterest(
      @AuthenticationPrincipal MemberDetails principal,
      @PathVariable String path, @RequestParam String interestName) {
    smallGroupService.removeSmallGroupInterest(principal, path, interestName);
    return ResponseEntity.ok().build();
  }
}
