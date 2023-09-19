package com.example.wegather.auth;


import com.example.wegather.global.vo.MemberType;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public MemberDto processNewMember(SignUpRequest request) {
    Member newMember = saveNewMember(request);
    newMember.generateEmailCheckToken();
    return MemberDto.from(newMember);
  }

  public Member saveNewMember(SignUpRequest request) {
    return memberRepository.save(Member.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .email(request.getEmail())
        .memberType(MemberType.ROLE_USER)
        .build());
  }

  public void signIn(String usernameOrEmail, String password) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
        usernameOrEmail, password);
    Authentication authenticate = authenticationManager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authenticate);
  }
}
