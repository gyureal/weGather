package com.example.wegather.member.domain;


import static com.example.wegather.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.wegather.global.exception.ErrorCode.USERNAME_DUPLICATED;

import com.example.wegather.global.auth.AuthenticationManager;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;

  @Transactional
  public MemberDto joinMember(JoinMemberRequest request) {
    if (memberRepository.existsByUsername(Username.of(request.getUsername()))) {
      throw new IllegalArgumentException(USERNAME_DUPLICATED.getDescription());
    }

    return MemberDto.from(memberRepository.save(Member.builder()
            .username(Username.of(request.getUsername()))
            .password(Password.of(request.getPassword(), passwordEncoder))
            .name(request.getName())
            .phoneNumber(PhoneNumber.of(request.getPhoneNumber()))
            .memberType(request.getMemberType())
        .build()));
  }

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
  public void updateProfileImage(Long id, MultipartFile profileImage) {
    Member member = getMember(id);
    validateUpdatable(member.getUsername().getValue());

    UploadFile uploadFile;
    uploadFile = storeFile.storeFile(profileImage);

    member.changeProfileImage(uploadFile.getStoreFileName());
  }

  @Transactional
  public void updateMemberAddress(Long id, AddressRequest addressRequest) {
    Member member = getMember(id);
    validateUpdatable(member.getUsername().getValue());

    member.changeAddress(addressRequest.convertAddressEntity());
  }

  @Transactional
  public List<InterestDto> addInterest(Long id, Long interestsId) {
    Member member = getMember(id);
    validateUpdatable(member.getUsername().getValue());

    Interest interest = findInterest(interestsId);

    member.addInterest(interest);
    return member.getInterestDtos();
  }

  private Interest findInterest(Long interestsId) {
    return interestRepository.findById(interestsId)
        .orElseThrow(() -> new IllegalArgumentException("관심사 ID를 찾을 수 없습니다."));
  }

  @Transactional
  public List<InterestDto> removeInterest(Long id, Long interestsId) {
    Member member = getMember(id);
    validateUpdatable(member.getUsername().getValue());

    Interest interest = findInterest(interestsId);

    member.removeInterest(interest);
    return member.getInterestDtos();
  }

  private void validateUpdatable(String username) {
    if (!isSelfOrAdmin(username)) {
      throw new AuthenticationException(DON_NOT_HAVE_AUTH_TO_UPDATE_MEMBER);
    }
  }

  private boolean isSelfOrAdmin(String username) {
    if(username.equals(authManager.getUsername()) || authManager.getPrincipal().isAdmin()) {
      return true;
    }
    return false;
  }
}
