package com.example.wegather.auth;


import com.example.wegather.global.exception.ErrorCode;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
  private final JavaMailSender javaMailSender;

  public MemberDto processNewMember(SignUpRequest request) {
    Member newMember = saveNewMember(request);
    newMember.generateEmailCheckToken();

    sendSignUpConfirmEmail(newMember);
    return MemberDto.from(newMember);
  }

  private void sendSignUpConfirmEmail(Member newMember) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(newMember.getEmail());
    mailMessage.setSubject("스터디올래, 회원 가입 인증");
    mailMessage.setText("/check-email-token?token=" + newMember.getEmailCheckToken() +
        "&email=" + newMember.getEmail());
    javaMailSender.send(mailMessage);
  }

  private Member saveNewMember(SignUpRequest request) {
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

  public Member verifyMember(String email, String token) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(
            () -> new IllegalArgumentException(ErrorCode.EMAIL_NOT_FOUND.getDescription()));
    if (!member.isValidToken(token)) {
      throw new IllegalArgumentException(ErrorCode.TOKEN_IS_NOT_VALID.getDescription());
    }
    member.completeSignUp();
    return member;
  }

  public void resendEmail(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

    member.generateEmailCheckToken();
    sendSignUpConfirmEmail(member);
  }
}
