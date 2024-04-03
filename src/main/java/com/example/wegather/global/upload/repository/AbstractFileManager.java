package com.example.wegather.global.upload.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractFileManager implements FileManager {
  private static final DateTimeFormatter simpleDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 저장할 파일 이름을 생성합니다.
   * 1. 생성한 날짜로 폴더를 구분합니다.
   * 2. Unique 한 이름을 생성하여 반환합니다.
   * 3. UUID 에 기존 파일의 확장자를 붙혀서 만듭니다.
   * @param originalFilename 기존 이미지명
   * @return 저장할 파일명 (ex) 2024/2024-01-29/UUID.jpeg)
   */
  public String createStoreFileName(String originalFilename) {
    // 날짜
    LocalDateTime now = LocalDateTime.now();
    int year = now.getYear();
    String todayDate = now.format(simpleDateFormatter);
    // 확장자
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString();
    // 조합
    return "images/" + year + "/" + todayDate + "/" + uuid + "." + ext;
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
}
