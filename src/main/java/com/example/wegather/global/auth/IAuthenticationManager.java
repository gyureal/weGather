package com.example.wegather.global.auth;

import com.example.wegather.auth.MemberDetails;
import org.springframework.security.core.Authentication;

public interface IAuthenticationManager {
  Authentication getAuthentication();

  MemberDetails getPrincipal();
}
