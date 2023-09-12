package com.example.wegather.member.domain;


import static com.example.wegather.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private static final String DON_NOT_HAVE_AUTH_TO_UPDATE_MEMBER = "회원을 수정할 권한이 없습니다.";
  private final MemberRepository memberRepository;
  private final InterestRepository interestRepository;
  private final StoreFile storeFile;


  public Page<Member> getAllMembers(Pageable pageable) {
    return memberRepository.findAll(pageable);
  }

  public MemberDto getMemberDto(Long id) {
    return MemberDto.from(getMember(id));
  }

  public Member getMember(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND.getDescription()));
  }

  @Transactional
  public void deleteMember(Long id) {
    memberRepository.deleteById(id);
  }

  @Transactional
  public void updateProfileImage(MemberDetails principal,Long id, MultipartFile profileImage) {
    Member member = getMember(id);
    validateUpdatable(principal, member.getUsername().getValue());

    UploadFile uploadFile;
    uploadFile = storeFile.storeFile(profileImage);

    member.changeProfileImage(uploadFile.getStoreFileName());
  }

  @Transactional
  public void updateMemberAddress(MemberDetails principal,Long id, AddressRequest addressRequest) {
    Member member = getMember(id);
    validateUpdatable(principal, member.getUsername().getValue());

    member.changeAddress(addressRequest.convertAddressEntity());
  }

  @Transactional
  public List<InterestDto> addInterest(MemberDetails principal,Long id, Long interestsId) {
    Member member = getMember(id);
    validateUpdatable(principal, member.getUsername().getValue());

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
    Member member = getMember(id);
    validateUpdatable(principal, member.getUsername().getValue());

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
}
