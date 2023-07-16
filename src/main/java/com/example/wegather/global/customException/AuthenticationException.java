package com.example.wegather.global.customException;

public class AuthenticationException extends RuntimeException{

  private static final String DO_NOT_HAVE_AUTHORITY = "권한이 없습니다.";

  public AuthenticationException() {
    super(DO_NOT_HAVE_AUTHORITY);
  }

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
