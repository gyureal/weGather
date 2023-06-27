package com.example.wegather.global.customException;

public class FileGetException extends RuntimeException {

  public FileGetException(String message) {
    super(message);
  }

  public FileGetException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
