package com.example.wegather.group.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.group.domain.service.SmallGroupJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
