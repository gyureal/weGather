package com.example.wegather.auth;

import com.example.wegather.auth.dto.MemberInfo;
import com.example.wegather.auth.dto.SignInRequest;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.global.exception.ErrorCode;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.dto.MemberDto;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {

  private final AuthService authService;
  private final MemberRepository memberRepository;

  private final SignUpRequestValidator signUpRequestValidator;

  @InitBinder("signUpRequest")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(signUpRequestValidator);
  }

  /**
   * 회원을 새로 추가합니다. (회원가입)
   * 회원가입 후 로그인 됩니다.
   * @param request 회원가입시 입력되는 회원정보
   * @return 생성된 회원에 접근할 수 있는 endpoint
   */
  @PostMapping("/sign-up")
  public ResponseEntity<MemberDto> signUp(@Valid @RequestBody SignUpRequest request) {
    // 회원가입
    MemberDto memberDto = authService.processNewMember(request);
    // 로그인
    authService.signIn(request.getUsername(), request.getPassword());
    return ResponseEntity.created(URI.create("/members/" + memberDto.getId())).body(memberDto);
  }

  /**
   * 로그인을 합니다.
   * @param request
   *    - username
   *    - password
   * @throws UsernameNotFoundException username 에 해당하는 회원을 찾지 못한 경우
   * @throws org.springframework.security.authentication.BadCredentialsException credential 이 일치하지 않는 경우
   */
  @PostMapping("/sign-in")
  public ResponseEntity<Void> signIn(@RequestBody @Valid SignInRequest request) {
    authService.signIn(request.getUsernameOrEmail(), request.getPassword());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/current-user")
  public ResponseEntity<MemberInfo> getMyInfo(@AuthenticationPrincipal MemberDetails memberDetails) {
    if (memberDetails != null) {
      Member member = memberRepository.findByUsername(memberDetails.getUsername())
          .orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
      return ResponseEntity.ok(MemberInfo.from(member));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }

  @PostMapping("/check-email-token")
  public ResponseEntity<String> checkEmailToken(@RequestParam String email, @RequestParam String token) {
    Member member = authService.verifyMember(email, token);
    return ResponseEntity.ok(member.getUsername());
  }

  @PostMapping("/resend-confirm-email")
  public ResponseEntity<Void> resendConfirmEmail(@AuthenticationPrincipal MemberDetails memberDetails) {
    authService.resendEmail(memberDetails.getMemberId());
    return ResponseEntity.ok().build();
  }
}
