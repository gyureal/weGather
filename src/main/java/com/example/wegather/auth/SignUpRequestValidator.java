package com.example.wegather.auth;

import com.example.wegather.auth.dto.SignUpRequest;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpRequestValidator implements Validator {
  private final MemberRepository memberRepository;
  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(SignUpRequest.class);
  }

  @Override
  public void validate(Object o, Errors errors) {
    SignUpRequest signUpRequest = (SignUpRequest) o;
    if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
      errors.rejectValue("email", "invalid.email",
          new Object[]{signUpRequest.getEmail()}, "이미 사용중인 이메일 입니다.");
    }

    if (memberRepository.existsByUsername(Username.of(signUpRequest.getUsername()))) {
      errors.rejectValue("username", "invalid.username", new Object[]{signUpRequest.getUsername()}, "이미 사용중인 아이디 입니다.");
    }
  }
}
