package com.example.wegather.member.web;

import com.example.wegather.member.domain.MemberService;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  /**
   * 회원을 새로 추가합니다.
   * @param request 회원가입시 입력되는 회원정보
   * @return 생성된 회원
   * @throws IllegalArgumentException
   *    회원 username 이 중복된 경우
   *    username, password, phoneNumber, address 등이 형식에 맞지 않는 경우
   */
  @PostMapping
  public ResponseEntity<MemberDto> createMember(@Valid @RequestBody JoinMemberRequest request) {
    MemberDto memberDto = MemberDto.from(memberService.joinMember(request));
    return ResponseEntity.created(URI.create("/members/" + memberDto.getId()))
        .body(memberDto);
  }

  /**
   * 전체 회원을 조회합니다.
   * @param pageRequest
   *    size: 페이지 사이즈
   *    page: 페이지 번호
   *    sort: 정렬 기준
   * @return 전체 관심사 목록
   */
  @GetMapping
  public ResponseEntity<Page<MemberDto>> readAllMember(Pageable pageRequest) {
    return ResponseEntity.ok(memberService.getAllInterests(pageRequest).map(MemberDto::from));
  }

  /**
   * id로 관심사를 조회합니다.
   * @param id
   * @return
   * @throws IllegalArgumentException id에 해당하는 관심사가 없는 경우 예외를 던집니다.
   */
  @GetMapping("/{id}")
  public ResponseEntity<MemberDto> readMemberById(@PathVariable Long id) {
    return ResponseEntity.ok(MemberDto.from(memberService.getMember(id)));
  }

  /**
   * id로 관심사를 삭제합니다.
   * @param id
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMemberById(@PathVariable Long id) {
    memberService.deleteMember(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 회원의 프로필 사진을 업데이트 합니다.
   * @param id
   * @param profileImage
   * @return
   */
  @PostMapping("/{id}/image")
  public ResponseEntity<Void> updateProfileImage(@PathVariable Long id, @RequestParam MultipartFile profileImage) {
    memberService.updateProfileImage(id, profileImage);
    return ResponseEntity.ok().build();
  }



}
