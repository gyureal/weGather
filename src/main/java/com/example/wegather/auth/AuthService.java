package com.example.wegather.auth;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.example.wegather.global.vo.PhoneNumber;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Password;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getDescription()));

    return MemberDetails.from(member);
  }

  @Transactional
  public MemberDto signUp(SignUpRequest request) {
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
}
