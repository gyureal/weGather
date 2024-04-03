package com.example.wegather.global.upload;

import com.example.wegather.global.upload.repository.AbstractStoreFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageUploadService {
  private final AbstractStoreFile storeFile;

  /**
   * MultipartFile 타입의 이미지를 업로드합니다.
   * @param multipartFile
   * @return 업로드 이미지 이름
   */
  public String uploadImage(MultipartFile multipartFile) {
    UploadFile uploadFile = storeFile.storeFile(multipartFile);
    return uploadFile.getStoredFileName();
  }

  /**
   * base64 형태의 이미지를 업로드합니다.
   * @param base64EncodedImage
   * @param originalImageName
   * @return 업로드된 이미지명
   */
  public String uploadImage(String base64EncodedImage, String originalImageName) {
    byte[] bytesImage = storeFile.decodeBase64Image(base64EncodedImage);
    UploadFile uploadFile = storeFile.storeFile(bytesImage, originalImageName);
    return uploadFile.getStoredFileName();
  }

  public void deleteImage(String storedImage) {
    if (StringUtils.hasText(storedImage)) {
      storeFile.deleteFile(storedImage);
    }
  }
}
