package com.example.wegather.auth;


import com.example.wegather.global.exception.ErrorCode;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.mail.EmailMessage;
import com.example.wegather.global.mail.EmailService;
import com.example.wegather.global.vo.MemberType;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.domain.entity.MemberAlarmSetting;
import com.example.wegather.member.dto.MemberDto;
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
  private final EmailService emailService;

  /**
   * 회원가입을 실시합니다.
   *    - 가입한 이메일로 회원 확인 이메일을 전송합니다.
   * @param request
   * @return
   */
  public MemberDto signUp(SignUpRequest request) {
    Member newMember = saveNewMember(request);
    newMember.generateEmailCheckToken();

    sendSignUpConfirmEmail(newMember);
    return MemberDto.from(newMember);
  }

  private void sendSignUpConfirmEmail(Member newMember) {
    EmailMessage emailMessage = EmailMessage.builder()
        .to("WeGather, 회원 가입 인증")
        .subject("WeGather, 회원 가입 인증")
        .message("/check-email-token?token=" + newMember.getEmailCheckToken() +
            "&email=" + newMember.getEmail())
        .build();

    emailService.sendEmail(emailMessage);
  }

  private Member saveNewMember(SignUpRequest request) {
    Member newMember = memberRepository.save(Member.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .email(request.getEmail())
        .memberType(MemberType.ROLE_USER)
        .build());

    newMember.changeMemberAlarmSetting(new MemberAlarmSetting());
    return newMember;
  }

  /**
   * 로그인을 합니다.
   * @param usernameOrEmail
   * @param password
   */
  public void signIn(String usernameOrEmail, String password) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
        usernameOrEmail, password);
    Authentication authenticate = authenticationManager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authenticate);
  }

  /**
   * 입력받은 이메일과 토큰으로 회원을 검증합니다.
   * @param email
   * @param token
   * @return
   */
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

  /**
   * 메일을 재전송합니다.
   * @param memberId
   */
  public void resendEmail(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

    member.generateEmailCheckToken();
    sendSignUpConfirmEmail(member);
  }
}
