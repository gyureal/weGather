package com.example.wegather.member.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.auth.dto.MemberProfileDto;
import com.example.wegather.global.exception.customException.FileUploadException;
import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.MemberService;
import com.example.wegather.member.dto.EditProfileForm;
import com.example.wegather.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   * 전체 회원을 조회합니다.
   * @param pageRequest
   *    size: 페이지 사이즈
   *    page: 페이지 번호
   *    sort: 정렬 기준
   * @return 전체 관심사 목록
   */
  @GetMapping
  public ResponseEntity<Page<MemberDto>> readAllMember(Pageable pageRequest) {
    return ResponseEntity.ok(memberService.getAllMembers(pageRequest).map(MemberDto::from));
  }

  /**
   * id로 회원을 조회합니다.
   * @param id
   * @return
   * @throws IllegalArgumentException id에 해당하는 관심사가 없는 경우 예외를 던집니다.
   */
  @GetMapping("/{id}")
  public ResponseEntity<MemberDto> readMemberById(@PathVariable Long id) {
    return ResponseEntity.ok(memberService.getMemberDto(id));
  }

  /**
   * 회원 아이디로 회원을 조회합니다.
   * @param username
   * @return
   */
  @GetMapping("/profile/{username}")
  public ResponseEntity<MemberProfileDto> readMemberByUsername(@PathVariable String username) {
    return ResponseEntity.ok(memberService.getMemberProfileByUsername(username));
  }

  @PostMapping("/profile")
  public ResponseEntity<Void> editProfile(@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody
      EditProfileForm editProfileForm) {
    memberService.editProfile(memberDetails.getId(),editProfileForm);
    return ResponseEntity.ok().build();
  }

  /**
   * id로 회원을 삭제합니다.
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
   * @throws IllegalArgumentException 회원이 존재하지 않은 경우
   * @throws FileUploadException
   *     이미지 업로드에 실패할 경우
   * @return
   */
  @PutMapping("/{id}/image")
  public ResponseEntity<Void> updateProfileImage(@AuthenticationPrincipal MemberDetails principal,
      @PathVariable Long id, @RequestParam MultipartFile profileImage) {
    memberService.updateProfileImage(principal, id, profileImage);
    return ResponseEntity.ok().build();
  }

  /**
   * 회원의 주소를 수정합니다.
   * @param id
   * @param addressRequest
   * @throws IllegalArgumentException
   *     일치하는 회원이 없을 때
   *     주소의 형식이 맞지 않을 때
   * @return
   */
  @PutMapping("/{id}/address")
  public ResponseEntity<Void> updateMemberAddress(@AuthenticationPrincipal MemberDetails principal,
      @PathVariable Long id, @RequestBody AddressRequest addressRequest) {
    memberService.updateMemberAddress(principal, id, addressRequest);
    return ResponseEntity.ok().build();
  }

  /**
   * 회원의 관심사를 추가합니다.
   * @param id 회원 ID
   * @param interestId 추가할 관심사 ID
   * @return
   */
  @PostMapping("/{id}/interests")
  public ResponseEntity<List<InterestDto>> addMemberInterests(@AuthenticationPrincipal MemberDetails principal,
      @PathVariable Long id, @RequestParam Long interestId) {
    return ResponseEntity.ok(memberService.addInterest(principal, id, interestId));
  }

  /**
   * 회원의 관심사를 제거합니다.
   * @param id 회원 ID
   * @param interestId 삭제할 관심사 ID
   * @return
   */
  @DeleteMapping("/{id}/interests")
  public ResponseEntity<List<InterestDto>> removeMemberInterests(@AuthenticationPrincipal MemberDetails principal,
      @PathVariable Long id, @RequestParam Long interestId) {
    return ResponseEntity.ok(memberService.removeInterest(principal, id, interestId));
  }
}
