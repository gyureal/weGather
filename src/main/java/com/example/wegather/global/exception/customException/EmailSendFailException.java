package com.example.wegather.global.exception.customException;

public class EmailSendFailException extends RuntimeException {
  public EmailSendFailException(String message) {
    super(message);
  }

  public EmailSendFailException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
