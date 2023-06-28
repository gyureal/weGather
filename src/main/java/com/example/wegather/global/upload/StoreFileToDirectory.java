package com.example.wegather.global.upload;

import com.example.wegather.global.customException.FileDeleteException;
import com.example.wegather.global.customException.FileGetException;
import com.example.wegather.global.customException.FileUploadException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
//@Service
public class StoreFileToDirectory implements StoreFile {

  private static final String FAIL_TO_UPLOAD_PROFILE_IMAGE = "회원 프로필 이미지 업로드에 실패했습니다.";
  private static final String FAIL_TO_GET_FILE = "파일을 가져오는데 실패했습니다.";
  private static final String FAIL_TO_DELETE_FILE = "파일 삭제에 실패했습니다.";
  @Value("${file.dir}")
  private String fileDir;

  @Override
  public Resource getFile(String filename) {
    try {
      return new UrlResource("file:" + getFullPath(filename));
    } catch (MalformedURLException e) {
      throw new FileGetException(FAIL_TO_GET_FILE);
    }
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
   * @throws FileUploadException
   */
  public UploadFile storeFile(MultipartFile multipartFile) {
    if (multipartFile.isEmpty()) {
      return null;
    }
    String originalFilename = multipartFile.getOriginalFilename();
    String storeFileName = createStoreFileName(originalFilename);
    log.info(storeFileName);
    try {
      multipartFile.transferTo(new File(getFullPath(storeFileName)));
    } catch (IOException e) {
      throw new FileUploadException(FAIL_TO_UPLOAD_PROFILE_IMAGE, e);
    }

    return new UploadFile(originalFilename, storeFileName);
  }

  /**
   * 파일을 삭제합니다.
   * @param filename
   */
  @Override
  public void deleteFile(String filename) {
    Path filePath = FileSystems.getDefault().getPath(getFullPath(filename));
    try {
      Files.delete(filePath);
    } catch (IOException | SecurityException e) {
      throw new FileDeleteException(FAIL_TO_DELETE_FILE);
    }
  }

  /**
   * 파일의 전체 경로를 반환합니다.
   * @param filename
   * @return
   */
  private String getFullPath(String filename) {
    return fileDir + filename;
  }
}
