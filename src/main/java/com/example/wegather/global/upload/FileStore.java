package com.example.wegather.global.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileStore {
  @Value("${file.dir}")
  private String fileDir;

  /**
   * 파일의 전체 경로를 반환합니다.
   * @param filename
   * @return
   */
  public String getFullPath(String filename) {
    return fileDir + filename;
  }

  /**
   * 여러 파일을 업로드 합니다.
   * @param multipartFiles
   * @return
   * @throws IOException
   */
  public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
    List<UploadFile> storeFileResult = new ArrayList<>();
    for (MultipartFile multipartFile : multipartFiles) {
      if (!multipartFile.isEmpty()) {
        storeFileResult.add(storeFile(multipartFile));
      }
    }
    return storeFileResult;
  }

  /**
   * 하나의 파일을 업로드 합니다.
   * @param multipartFile
   * @return UploadFile
   * @throws IOException
   */
  public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
    if (multipartFile.isEmpty()) {
      return null;
    }
    String originalFilename = multipartFile.getOriginalFilename();
    String storeFileName = createStoreFileName(originalFilename);
    log.info(storeFileName);
    multipartFile.transferTo(new File(getFullPath(storeFileName)));

    return new UploadFile(originalFilename, storeFileName);
  }

  private String createStoreFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return uuid + "." + ext;
  }

  private String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }
}
