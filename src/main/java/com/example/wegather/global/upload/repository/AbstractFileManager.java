package com.example.wegather.global.upload.repository;

import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractFileManager implements FileManager {

  /**
   * Base64 파일을 decode 하여 반환합니다.
   * MultipartFile 타입의 입력값을 사용하도록 권장 (base64 입력값은 Deprecated 됨)
   * @param base64File
   * @return
   */
  @Deprecated
  public byte[] decodeBase64Image(String base64File) {
    String encodedImage = parseOnlyImageFromBase64(base64File);
    return Base64.decodeBase64(encodedImage);
  }

  /**
   * Base64 형태에서 파일 정보만 추출합니다.
   * MultipartFile 타입의 입력값을 사용하도록 권장 (base64 입력값은 Deprecated 됨)
   * ex) `data:image/png;base64,iVBORw0KGgoAAAANSUh....` 에서 `data:image/png;base64,` 이후를 반환합니다.
   * @param base64File
   * @return
   */
  @Deprecated
  private String parseOnlyImageFromBase64(String base64File) {
    return base64File.split(",")[1];
  }

  /**
   * 저장할 파일 이름을 생성합니다.
   * Unique 한 이름을 생성하여 반환합니다.
   * UUID 에 기존 파일의 확장자를 붙혀서 만듭니다.
   * @param originalFilename
   * @return
   */
  String createStoreFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return uuid + "." + ext;
  }

  /**
   * 파일의 확장자를 반환합니다.
   * @param originalFilename
   * @return
   */
  String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }
}
