package com.example.wegather.smallGroupJoin.web;

import com.example.wegather.smallGroupJoin.domin.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.SmallGroupJoinService;
import com.example.wegather.smallGroupJoin.dto.SmallGroupMemberDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/smallGroups/{id}/join")
@RestController
public class SmallGroupJoinController {

  private final SmallGroupJoinService smallGroupJoinService;

  @PostMapping
  public ResponseEntity<Void> joinSmallGroup(@PathVariable Long id) {
    smallGroupJoinService.joinGroup(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<Page<SmallGroupMemberDto>> readSmallGroupJoinMember(
      @PathVariable Long id,
      @RequestParam Optional<MemberStatus> status,
      Pageable pageable) {

    return ResponseEntity.ok(smallGroupJoinService.readJoinMember(id, status, pageable)
        .map(SmallGroupMemberDto::from));
  }
}
