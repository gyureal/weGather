package com.example.wegather.member.domain;


import static com.example.wegather.global.exception.ErrorCode.INTEREST_NOT_FOUND;
import static com.example.wegather.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.wegather.global.exception.ErrorCode.PASSWORD_NOT_MATCHED;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.interest.domain.InterestService;
import com.example.wegather.member.dto.ChangeAlarmSettingsForm;
import com.example.wegather.member.dto.ChangePasswordForm;
import com.example.wegather.member.dto.EditProfileImageRequest;
import com.example.wegather.member.dto.MemberProfileDto;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.EditProfileForm;
import com.example.wegather.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private static final String DON_NOT_HAVE_AUTH_TO_UPDATE_MEMBER = "회원을 수정할 권한이 없습니다.";
  private final MemberRepository memberRepository;
  private final InterestRepository interestRepository;
  private final InterestService interestService;
  private final StoreFile storeFile;
  private final PasswordEncoder passwordEncoder;


  public Page<Member> getAllMembers(Pageable pageable) {
    return memberRepository.findAll(pageable);
  }

  public MemberDto getMemberDto(Long id) {
    return MemberDto.from(getMemberById(id));
  }



  @Transactional
  public void deleteMember(Long id) {
    memberRepository.deleteById(id);
  }

  @Transactional
  public void updateProfileImage(Long memberId, EditProfileImageRequest request) {
    Member member = getMemberById(memberId);

    byte[] imageBytes = storeFile.decodeBase64Image(request.getImage());
    UploadFile uploadFile = storeFile.storeFile(imageBytes, request.getOriginalImageName());

    member.changeProfileImage(uploadFile.getStoreFileName());
  }

  @Transactional
  public void updateMemberAddress(MemberDetails principal,Long id, AddressRequest addressRequest) {
    Member member = getMemberById(id);
    validateUpdatable(principal, member.getUsername());

    member.changeAddress(addressRequest.convertAddressEntity());
  }

  @Transactional
  public List<InterestDto> addInterest(MemberDetails principal,Long id, Long interestsId) {
    Member member = getMemberById(id);
    validateUpdatable(principal, member.getUsername());

    Interest interest = findInterest(interestsId);

    member.addInterest(interest);
    return member.getInterestDtos();
  }

  private Interest findInterest(Long interestsId) {
    return interestRepository.findById(interestsId)
        .orElseThrow(() -> new IllegalArgumentException("관심사 ID를 찾을 수 없습니다."));
  }

  @Transactional
  public List<InterestDto> removeInterest(MemberDetails principal, Long id, Long interestsId) {
    Member member = getMemberById(id);
    validateUpdatable(principal, member.getUsername());

    Interest interest = findInterest(interestsId);

    member.removeInterest(interest);
    return member.getInterestDtos();
  }

  private void validateUpdatable(MemberDetails principal, String username) {
    if (!isSelfOrAdmin(principal, username)) {
      throw new AuthenticationException(DON_NOT_HAVE_AUTH_TO_UPDATE_MEMBER);
    }
  }

  private boolean isSelfOrAdmin(MemberDetails principal, String username) {
    if(username.equals(principal.getUsername()) || principal.isAdmin()) {
      return true;
    }
    return false;
  }

  public MemberProfileDto getMemberProfileByUsername(String username) {
    return MemberProfileDto.from(memberRepository.findWithAlarmSettingByUsername(username).orElseThrow(
        () -> new IllegalArgumentException(MEMBER_NOT_FOUND.getDescription())));
  }

  @Transactional
  public void editProfile(Long memberId, EditProfileForm form) {
    Member member = getMemberById(memberId);
    member.editProfile(form.getIntroductionText());
  }

  @Transactional
  public void changePassword(Long memberId, ChangePasswordForm form) {
    Member member = getMemberById(memberId);
    if (!passwordEncoder.matches(form.getOriginalPassword(), member.getPassword())) {
      throw new IllegalArgumentException(PASSWORD_NOT_MATCHED.getDescription());
    }
    member.changePassword(passwordEncoder.encode(form.getNewPassword()));
  }

  @Transactional
  public void changeProfileAlarmSettings(Long memberId, ChangeAlarmSettingsForm changeAlarmSettingsForm) {
    Member member = getMemberById(memberId);
    member.changeMemberAlarmSetting(changeAlarmSettingsForm.toEntity());
  }

  /**
   * 관심사 명으로 관심사를 추가합니다.
   * @param memberId
   * @param interestsName
   */
  @Transactional
  public void addInterestByName(Long memberId, String interestsName) {
    Member member = getMemberById(memberId);
    Interest interest = interestService.findOrAddInterestByName(interestsName);
    member.addInterest(interest);
  }

  @Transactional
  public void removeInterestByName(Long memberId, String interestName) {
    Member member = getMemberById(memberId);
    Interest interest = interestRepository.findByName(interestName)
            .orElseThrow(() -> new IllegalArgumentException(INTEREST_NOT_FOUND.getDescription()));
    member.removeInterest(interest);
  }

  public List<String> getMyInterests(Long memberId) {
    Member member = getWithInterestsAndAlarmById(memberId);
    return member.getInterestsName();
  }

  private Member getWithInterestsAndAlarmById(Long memberId) {
    return memberRepository.findWithInterestsAndAlarmById(memberId)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND.getDescription()));
  }

  private Member getMemberById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND.getDescription()));
  }
}
