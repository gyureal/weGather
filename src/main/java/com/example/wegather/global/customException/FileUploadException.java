package com.example.wegather.global.customException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadException extends RuntimeException {
  public FileUploadException(String message) {
    super(message);
  }

  public FileUploadException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
