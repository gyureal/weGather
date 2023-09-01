package com.example.wegather.smallGroupJoin.web;

import com.example.wegather.global.customException.AuthenticationException;
import com.example.wegather.smallGroupJoin.domin.vo.MemberStatus;
import com.example.wegather.smallGroupJoin.domin.service.SmallGroupJoinService;
import com.example.wegather.smallGroupJoin.dto.SmallGroupMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/smallGroups/{id}")
@RestController
public class SmallGroupJoinController {

  private final SmallGroupJoinService smallGroupJoinService;

  /**
   * 소모임에 가입 합니다.
   * 요청과 동시에 승인 됩니다.
   * @param id 소그룹 아이디
   * @throws IllegalArgumentException
   *     소모임 정보를 찾지 못했을 때
   *     회원 정보를 찾지 못했을 때
   *     이미 가입한 회원일 때
   */
  @PostMapping("/join")
  public ResponseEntity<Void> joinSmallGroup(@PathVariable Long id) {
    smallGroupJoinService.joinGroup(id);
    return ResponseEntity.ok().build();
  }

  /**
   * 가입요청한 회원을 조회합니다.
   * @param id 소모임 Id
   * @param status 가입요청 상태
   * @param pageable 페이징 파라메터
   * @throws IllegalArgumentException 소모임을 찾을 수 없을 때
   * @throws AuthenticationException 소모임장이나 관리자가 아닐때
   * @return
   */
  @GetMapping("/join")
  public ResponseEntity<Page<SmallGroupMemberDto>> readSmallGroupJoinMember(
      @PathVariable Long id,
      @RequestParam @Nullable MemberStatus status,
      Pageable pageable) {

    return ResponseEntity.ok(smallGroupJoinService.readJoinMember(id, status, pageable)
        .map(SmallGroupMemberDto::from));
  }

  @PostMapping("/leave")
  public ResponseEntity<Void> leaveSmallGroup(@PathVariable Long id, @RequestParam Long memberId) {
    smallGroupJoinService.leave(id, memberId);
    return ResponseEntity.ok().build();
  }
}
