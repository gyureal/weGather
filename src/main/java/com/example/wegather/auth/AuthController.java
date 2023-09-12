package com.example.wegather.auth;

import com.example.wegather.auth.dto.SignInRequest;
import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.dto.MemberDto;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;

  /**
   * 회원을 새로 추가합니다. (회원가입)
   * @param request 회원가입시 입력되는 회원정보
   * @return 생성된 회원에 접근할 수 있는 endpoint
   * @throws IllegalArgumentException
   *    회원 username 이 중복된 경우
   *    username, password, phoneNumber, address 등이 형식에 맞지 않는 경우
   */
  @PostMapping("/sign-up")
  public ResponseEntity<MemberDto> signUp(@Valid @RequestBody SignUpRequest request) {
    MemberDto memberDto = authService.signUp(request);
    return ResponseEntity.created(URI.create("/members/" + memberDto.getId())).body(memberDto);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<Void> signIn(@RequestBody SignInRequest request) {
    authService.signIn(request);
    return ResponseEntity.ok().build();
  }
}
