package com.example.wegather.global.customException;

public class FileDeleteException extends RuntimeException {
  public FileDeleteException(String message) {
    super(message);
  }

  public FileDeleteException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
