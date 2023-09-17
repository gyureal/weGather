package com.example.wegather.auth;

import com.example.wegather.auth.dto.SignInRequest;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;

  private final SignUpRequestValidator signUpRequestValidator;

  @InitBinder("signUpRequest")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(signUpRequestValidator);
  }

  /**
   * 회원을 새로 추가합니다. (회원가입)
   * @param request 회원가입시 입력되는 회원정보
   * @return 생성된 회원에 접근할 수 있는 endpoint
   */
  @PostMapping("/sign-up")
  public ResponseEntity<MemberDto> signUp(@Valid @RequestBody SignUpRequest request) {
    MemberDto memberDto = authService.signUp(request);
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
  public ResponseEntity<Void> signIn(@RequestBody SignInRequest request) {
    authService.signIn(request);
    return ResponseEntity.ok().build();
  }
}
