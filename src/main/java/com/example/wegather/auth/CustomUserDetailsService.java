package com.example.wegather.auth;

import static com.example.wegather.global.exception.ErrorCode.USERNAME_NOT_FOUND;

import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getDescription()));

    return MemberDetails.from(member);
  }
}
