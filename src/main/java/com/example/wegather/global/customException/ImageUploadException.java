package com.example.wegather.global.customException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageUploadException extends RuntimeException {
  public ImageUploadException(String message) {
    super(message);
  }

  public ImageUploadException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
