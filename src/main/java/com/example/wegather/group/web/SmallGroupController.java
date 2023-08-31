package com.example.wegather.group.web;

import com.example.wegather.group.domain.SmallGroupService;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.interest.dto.InterestDto;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  /**
   * 소모임을 생성합니다.
   * @param createSmallGroupRequest 소모임 생성 정보 dto
   * @param principal 로그인한 회원의 정보
   * @return 생성된 소모임
   */
  @PostMapping
  public ResponseEntity<SmallGroupDto> createGroup(
      @Valid @RequestBody CreateSmallGroupRequest createSmallGroupRequest,
      Principal principal) {
    SmallGroupDto smallGroupDto = SmallGroupDto.from(
        smallGroupService.addSmallGroup(createSmallGroupRequest, principal.getName()));
    return ResponseEntity.created(URI.create("/smallGroups/" + smallGroupDto.getId()))
        .body(smallGroupDto);
  }

  /**
   * 소모임을 조회합니다.
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public ResponseEntity<SmallGroupDto> readGroup(@PathVariable Long id) {
    return ResponseEntity.ok(SmallGroupDto.from(smallGroupService.getSmallGroup(id)));
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
      @PathVariable Long id,
      @RequestBody UpdateSmallGroupRequest request) {

    smallGroupService.editSmallGroup(id, request);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임을 삭제합니다.
   * @param id 소모임 id
   * @return
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
    smallGroupService.deleteSmallGroup(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 소모임에 관심사를 추가합니다.
   * @param id
   * @param interestId
   * @return
   */
  @PostMapping("/{id}/interest")
  public ResponseEntity<Void> addInterest(
      @PathVariable Long id, @RequestParam Long interestId) {
    smallGroupService.addSmallGroupInterest(id, interestId);
    return ResponseEntity.ok().build();
  }

  /**
   * 소모임에 관심사를 삭제합니다.
   * @param id
   * @param interestId
   * @return
   */
  @DeleteMapping("/{id}/interest")
  public ResponseEntity<List<InterestDto>> removeInterest(
      @PathVariable Long id, @RequestParam Long interestId) {
    smallGroupService.removeSmallGroupInterest(id, interestId);
    return ResponseEntity.ok().build();
  }
}
