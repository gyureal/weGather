package com.example.wegather.global.auth;

import com.example.wegather.auth.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationManager implements IAuthenticationManager {

  private static final String CANNOT_GET_PRINCIPAL_AS_MEMBER_DETAILS_TYPE = "MemberDetails 타입으로 Principal을 꺼낼 수 없습니다.";

  @Override
  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Override
  public MemberDetails getPrincipal() {
    Object principal = getAuthentication().getPrincipal();
    if (principal instanceof MemberDetails) {
      return (MemberDetails) principal;
    }
    throw new IllegalStateException(CANNOT_GET_PRINCIPAL_AS_MEMBER_DETAILS_TYPE);
  }

  @Override
  public String getUsername() {
    return getPrincipal().getUsername();
  }
}
