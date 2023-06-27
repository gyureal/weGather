package com.example.wegather.global.upload;

import java.util.UUID;
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
   * 저장할 파일 이름을 생성합니다.
   * UUID 에 기존 파일의 확장자를 붙혀서 만듭니다.
   * @param originalFilename
   * @return
   */
  default String createStoreFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return uuid + "." + ext;
  }

  /**
   * 파일의 확장자를 반환합니다.
   * @param originalFilename
   * @return
   */
  default String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }
}
