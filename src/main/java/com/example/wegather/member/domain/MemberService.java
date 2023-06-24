package com.example.wegather.member.domain;

import static com.example.wegather.global.Message.Error.MEMBER_NOT_FOUND;
import static com.example.wegather.global.Message.Error.USERNAME_DUPLICATED;

import com.example.wegather.global.vo.Address;
import com.example.wegather.global.vo.Image;
import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.member.dto.JoinMemberRequest;
import com.example.wegather.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

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
            .profileImage(Image.of(request.getProfileImage()))
        .build());
  }

  public List<Member> getAllInterests() {
    return memberRepository.findAll();
  }

  public Member getMember(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND));
  }

  public void deleteMember(Long id) {
    memberRepository.deleteById(id);
  }
}
