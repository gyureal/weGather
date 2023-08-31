package com.example.wegather.auth;

import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private static final String USERNAME_NOT_FOUND = "사용자 정보를 찾을 수 없습니다.";
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND));

    return MemberDetails.from(member);
  }
}
