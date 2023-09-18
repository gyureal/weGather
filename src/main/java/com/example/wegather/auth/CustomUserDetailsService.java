package com.example.wegather.auth;

import static com.example.wegather.global.exception.ErrorCode.USERNAME_NOT_FOUND;

import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import java.util.Optional;
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
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    Optional<Member> member = memberRepository.findByEmail(usernameOrEmail);
    if (member.isEmpty()) {
      member = memberRepository.findByUsername(usernameOrEmail);
    }

    return MemberDetails.from(member
        .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getDescription()))
    );
  }
}
