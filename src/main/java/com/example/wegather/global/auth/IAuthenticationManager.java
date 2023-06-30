package com.example.wegather.global.auth;

import org.springframework.security.core.Authentication;

public interface IAuthenticationManager {
  Authentication getAuthentication();
}
