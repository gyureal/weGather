package com.example.wegather.group.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.group.domain.service.SmallGroupService;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.ManagerAndMemberDto;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateBannerRequest;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.group.validator.CreateSmallGroupValidator;
import com.example.wegather.interest.dto.InterestDto;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;


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
   * 소모임 배너 수정
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
   * 소모임을 검색합니다.
   * @param cond 소모임 검색 조건
   * @param pageable 페이징 정보
   * @return 페이징 정보를 포함한 소모임 목록
   */
  @GetMapping
  public ResponseEntity<Page<SmallGroupDto>> searchGroups(
      @RequestBody SmallGroupSearchCondition cond,
      Pageable pageable) {
    return ResponseEntity.ok(smallGroupService.searchSmallGroups(cond, pageable).map(SmallGroupDto::from));
  }

  /**
   * 소모임 정보를 업데이트 합니다.
   * @param id 소모임 id
   * @param request 소모임 update dto
   * @return
   */
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGroup(
      @AuthenticationPrincipal MemberDetails principal,
      @PathVariable Long id,
      @RequestBody UpdateSmallGroupRequest request) {

    smallGroupService.editSmallGroup(principal, id, request);
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
