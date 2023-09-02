package com.example.wegather.group.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.group.domain.service.SmallGroupJoinService;
import com.example.wegather.group.dto.GroupJoinRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/smallGroups/{id}/join")
@RestController
public class SmallGroupJoinController {
  private final SmallGroupJoinService smallGroupJoinService;

  @PostMapping("/requests")
  public ResponseEntity<Void> requestSmallGroupJoin(@PathVariable Long id, @AuthenticationPrincipal
      MemberDetails memberDetails) {
    smallGroupJoinService.requestJoin(id, memberDetails.getMemberId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/requests")
  public ResponseEntity<Page<GroupJoinRequestDto>> readAllJoinRequests(
      @PathVariable Long id,
      @AuthenticationPrincipal MemberDetails memberDetails,
      Pageable pageable) {
    return ResponseEntity.ok(smallGroupJoinService.getAllJoinRequests(
        id, memberDetails.getMemberId() ,pageable));
  }
}
