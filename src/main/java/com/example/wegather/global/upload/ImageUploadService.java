package com.example.wegather.global.upload;

import com.example.wegather.global.exception.ErrorCode;
import com.example.wegather.global.upload.entity.FileUploadHistory;
import com.example.wegather.global.upload.entity.FileUploadStatus;
import com.example.wegather.global.upload.entity.StorageType;
import com.example.wegather.global.upload.repository.AbstractFileManager;
import com.example.wegather.global.upload.repository.FileUploadHistoryRepository;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ImageUploadService {
  private final AbstractFileManager fileManager;
  private final FileUploadHistoryRepository fileUploadHistoryRepository;
  private final MemberRepository memberRepository;

  public Resource downloadImage(String filename) {
    return fileManager.getFile(filename);
  }

  /**
   * MultipartFile 타입의 이미지를 업로드합니다.
   * @param multipartFile
   * @return 업로드 이미지 이름
   */
  @Transactional
  public String uploadImage(MultipartFile multipartFile, Long uploadMemberId) {
    UploadFile uploadFile = fileManager.storeFile(multipartFile);
    saveNewUploadHistory(uploadFile, uploadMemberId);
    return uploadFile.getStoredFileName();
  }

  /**
   * base64 형태의 이미지를 업로드합니다.
   * @param base64EncodedImage
   * @param originalImageName
   * @return 업로드된 이미지명
   */
  @Deprecated
  public String uploadImage(String base64EncodedImage, String originalImageName) {
    byte[] bytesImage = fileManager.decodeBase64Image(base64EncodedImage);
    UploadFile uploadFile = fileManager.storeFile(bytesImage, originalImageName);
    return uploadFile.getStoredFileName();
  }

  private void saveNewUploadHistory(UploadFile uploadFile, Long uploadMemberId) {
    Member uploadMember = memberRepository.findById(uploadMemberId)
        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

    fileUploadHistoryRepository.save(FileUploadHistory.builder()
            .storedFileName(uploadFile.getStoredFileName())
            .originalFileName(uploadFile.getOriginalFileName())
            .member(uploadMember)
            .storageType(StorageType.AWS_S3)
            .fileUploadStatus(FileUploadStatus.UPLOADED)
        .build());
  }

  @Transactional
  public void deleteImage(String storedImage) {
    if (StringUtils.hasText(storedImage)) {
      fileManager.deleteFile(storedImage);
      try {
        FileUploadHistory fileUploadHistory = findFileUploadHistory(storedImage);
        fileUploadHistory.deleted();
      } catch (Exception e) {
        log.error("## deleteImage: ", e);
      }
    }
  }

  private FileUploadHistory findFileUploadHistory(String storedImage) {
    return fileUploadHistoryRepository.findByStoredFileName(storedImage)
        .orElseThrow(() -> new IllegalArgumentException(
            ErrorCode.FILE_UPLOAD_HISTORY_NOT_EXISTS.getDescription()));
  }
}
