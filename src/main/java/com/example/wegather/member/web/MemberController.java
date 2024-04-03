package com.example.wegather.member.web;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.member.dto.ChangeAlarmSettingsForm;
import com.example.wegather.member.dto.ChangePasswordForm;
import com.example.wegather.member.dto.EditProfileImageRequest;
import com.example.wegather.member.dto.MemberProfileDto;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.MemberService;
import com.example.wegather.member.dto.EditProfileForm;
import com.example.wegather.member.dto.MemberDto;
import com.example.wegather.member.dto.ProfileSmallGroupDto;
import com.example.wegather.member.validator.ChangePasswordFormValidator;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  private final ChangePasswordFormValidator changePasswordFormValidator;

  @InitBinder("changePasswordForm")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(changePasswordFormValidator);
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
   * MultipartFile 타입의 이미지로 입력받는 기능으로 대체되었습니다. (Deprecated)
   * @param memberDetails
   * @param editProfileImageRequest
   * @return
   */
  @Deprecated
  @PostMapping("/profile/image")
  public ResponseEntity<Void> editProfileImage(@AuthenticationPrincipal MemberDetails memberDetails,
      @RequestBody EditProfileImageRequest editProfileImageRequest) {
    memberService.updateProfileImage(memberDetails.getId(), editProfileImageRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/profile/image/v2")
  public ResponseEntity<Void> editProfileImage(@AuthenticationPrincipal MemberDetails memberDetails,
      MultipartFile multipartImage) {
    memberService.updateProfileImageMultipart(memberDetails.getId(), multipartImage);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/profile/password")
  public ResponseEntity<Void> changePassword(@AuthenticationPrincipal MemberDetails memberDetails,
      @RequestBody ChangePasswordForm changePasswordForm) {
    memberService.changePassword(memberDetails.getId(), changePasswordForm);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/profile/alarmSettings")
  public ResponseEntity<Void> changeProfileAlarmSettings(@AuthenticationPrincipal MemberDetails memberDetails,
      @RequestBody ChangeAlarmSettingsForm changeAlarmSettingsForm) {
    memberService.changeProfileAlarmSettings(memberDetails.getId(), changeAlarmSettingsForm);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/profile/interests")
  public ResponseEntity<Void> addInterestInProfile(@AuthenticationPrincipal MemberDetails memberDetails,
      @RequestParam String interestName) {
    memberService.addInterestByName(memberDetails.getId(), interestName);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/profile/interests")
  public ResponseEntity<Void> removeInterestInProfile(@AuthenticationPrincipal MemberDetails memberDetails,
      @RequestParam String interestName) {
    memberService.removeInterestByName(memberDetails.getId(), interestName);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/profile/interests")
  public ResponseEntity<List<String>> getInterestsInProfile(@AuthenticationPrincipal MemberDetails memberDetails) {
    List<String> myInterests = memberService.getMyInterests(memberDetails.getId());
    return ResponseEntity.ok(myInterests);
  }

  /**
   * 로그인한 회원이 가입한 소모임 목록을 반환합니다.
   * @param memberDetails
   * @return
   */
  @GetMapping("/profile/smallGroups/join")
  public ResponseEntity<List<ProfileSmallGroupDto>> getJoinSmallGroups(@AuthenticationPrincipal MemberDetails memberDetails) {
    List<ProfileSmallGroupDto> joinSmallGroups = memberService.getJoinSmallGroups(memberDetails);
    return ResponseEntity.ok(joinSmallGroups);
  }

  /**
   * 로그인한 회원이 생성한 소모임 목록을 반환합니다.
   * @param memberDetails
   * @return
   */
  @GetMapping("/profile/smallGroups/create")
  public ResponseEntity<List<ProfileSmallGroupDto>> getCreateSmallGroups(@AuthenticationPrincipal MemberDetails memberDetails) {
    List<ProfileSmallGroupDto> createSmallGroups = memberService.getCreateSmallGroups(memberDetails);
    return ResponseEntity.ok(createSmallGroups);
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
