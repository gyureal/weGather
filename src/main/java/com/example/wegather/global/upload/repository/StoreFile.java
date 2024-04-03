package com.example.wegather.global.upload.repository;

import com.example.wegather.global.upload.UploadFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StoreFile {

  /**
   * 해당 파일을 반환합니다.
   * @param filename
   * @return
   */
  Resource getFile(String filename);

  /**
   * 하나의 파일을 업로드 합니다.
   * @param multipartFile
   * @return
   */
  UploadFile storeFile(MultipartFile multipartFile);

  /**
   * byte[] 의 파일을 업로드 합니다.
   * @param bytes 이미지의 byte 배열
   * @param originalName 이미지의 원래 이름
   * @return
   */
  UploadFile storeFile(byte[] bytes, String originalName);

  /**
   * 파일을 삭제합니다.
   * @param filename
   */
  void deleteFile(String filename);
}
