package com.example.wegather.global.upload;

import lombok.Getter;

@Getter
public class UploadFile {
  private String uploadFileName;  // 기존의 파일 이름
  private String storeFileName;   // 업로드된 파일의 이름

  public UploadFile(String uploadFileName, String storeFileName) {
    this.uploadFileName = uploadFileName;
    this.storeFileName = storeFileName;
  }

  public static UploadFile of(String uploadFileName, String storeFileName) {
    return new UploadFile(uploadFileName, storeFileName);
  }
}
