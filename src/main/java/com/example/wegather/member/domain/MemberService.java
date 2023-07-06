package com.example.wegather.member.domain;

import static com.example.wegather.global.Message.Error.MEMBER_NOT_FOUND;
import static com.example.wegather.global.Message.Error.USERNAME_DUPLICATED;

import com.example.wegather.global.auth.AuthenticationManager;
import com.example.wegather.global.customException.AuthenticationException;
import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.interest.domain.Interests;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.member.dto.JoinMemberRequest;
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
  private final StoreFile storeFile;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;

  @Transactional
  public Member joinMember(JoinMemberRequest request) {
    if (memberRepository.existsByUsername(Username.of(request.getUsername()))) {
      throw new IllegalArgumentException(USERNAME_DUPLICATED);
    }

    return memberRepository.save(Member.builder()
            .username(Username.of(request.getUsername()))
            .password(Password.of(request.getPassword(), passwordEncoder))
            .name(request.getName())
            .phoneNumber(PhoneNumber.of(request.getPhoneNumber()))
            .address(Address.of(request.getStreetAddress()
                , request.getLongitude()
                , request.getLatitude()))
            .memberType(request.getMemberType())
            .profileImage(Image.of("default.jpg"))
            .interests(Interests.of(request.getInterests()))
        .build());
  }

  public Page<Member> getAllInterests(Pageable pageable) {
    return memberRepository.findAll(pageable);
  }

  public Member getMember(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND));
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

  public void updateInterests(Long id, List<String> interests) {
    Member member = getMember(id);
    validateUpdatable(member.getUsername().getValue());

    member.changeInterests(interests);
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
