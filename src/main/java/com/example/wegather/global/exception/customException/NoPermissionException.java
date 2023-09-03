package com.example.wegather.global.exception.customException;

public class NoPermissionException extends RuntimeException {
  public NoPermissionException(String message) {
    super(message);
  }

  public NoPermissionException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
