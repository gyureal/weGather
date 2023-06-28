package com.example.wegather.member.domain;

import static com.example.wegather.global.Message.Error.MEMBER_NOT_FOUND;
import static com.example.wegather.global.Message.Error.USERNAME_DUPLICATED;

import com.example.wegather.global.dto.AddressRequest;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.member.dto.JoinMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final StoreFile storeFile;

  @Transactional
  public Member joinMember(JoinMemberRequest request) {
    if (memberRepository.existsByUsername(Username.of(request.getUsername()))) {
      throw new IllegalArgumentException(USERNAME_DUPLICATED);
    }

    return memberRepository.save(Member.builder()
            .username(Username.of(request.getUsername()))
            .password(Password.of(request.getPassword()))
            .name(request.getName())
            .phoneNumber(PhoneNumber.of(request.getPhoneNumber()))
            .address(Address.of(request.getStreetAddress()
                , request.getLongitude()
                , request.getLatitude()))
            .memberType(request.getMemberType())
            .profileImage(Image.of("default.jpg"))
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

    UploadFile uploadFile;
    uploadFile = storeFile.storeFile(profileImage);

    member.changeProfileImage(uploadFile.getStoreFileName());
  }

  @Transactional
  public void updateMemberAddress(Long id, AddressRequest addressRequest) {
    Member member = getMember(id);
    member.changeAddress(addressRequest.convertAddressEntity());
  }
}
