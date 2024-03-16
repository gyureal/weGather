package com.example.wegather.group.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.group.domain.service.SmallGroupJoinService;
import com.example.wegather.group.dto.GroupJoinRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/smallGroups/{id}/join")
@RestController
public class SmallGroupJoinController {
  private final SmallGroupJoinService smallGroupJoinService;

  /**
   * 소모임 가입 요청
   * @param id 소모임 ID
   * @param memberDetails 로그인한 회원
   * @return 생성된 가입 ID
   */
  @PostMapping("/requests")
  public ResponseEntity<Long> requestSmallGroupJoin(@PathVariable Long id, @AuthenticationPrincipal
      MemberDetails memberDetails) {
    Long joinId = smallGroupJoinService.joinSmallGroup(id, memberDetails.getMemberId());
    return ResponseEntity.ok(joinId);
  }

  /**
   * 소모임 가입 요청 회원 목록 조회
   * @param id 소모임 ID
   * @param memberDetails 로그인한 회원
   * @return
   */
  @GetMapping("/requests")
  public ResponseEntity<List<GroupJoinRequestDto>> readAllJoinRequests(
      @PathVariable Long id,
      @AuthenticationPrincipal MemberDetails memberDetails) {
    return ResponseEntity.ok(smallGroupJoinService.getAllJoinRequests(id, memberDetails.getMemberId()));
  }

  @PostMapping("/requests/{requestId}/approve")
  public ResponseEntity<Void> approveJoinRequest(
      @PathVariable Long id, @PathVariable Long requestId,
      @AuthenticationPrincipal MemberDetails memberDetails) {
    smallGroupJoinService.approveJoinRequest(id, requestId, memberDetails.getMemberId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/requests/{requestId}/reject")
  public ResponseEntity<Void> rejectJoinRequest(
      @PathVariable Long id, @PathVariable Long requestId,
      @AuthenticationPrincipal MemberDetails memberDetails) {
    smallGroupJoinService.rejectJoinRequest(id, requestId, memberDetails.getMemberId());
    return ResponseEntity.ok().build();
  }
}
