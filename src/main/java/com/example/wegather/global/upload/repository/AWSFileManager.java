package com.example.wegather.global.upload.repository;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.wegather.global.exception.customException.FileUploadException;
import com.example.wegather.global.upload.UploadFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AWSFileManager extends AbstractFileManager {

  @Value("${application.bucket.name}")
  private String bucketName;

  private final AmazonS3 s3Client;

  @Override
  public Resource getFile(String filename) {
    S3Object s3Object = s3Client.getObject(bucketName, filename);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();
    return new InputStreamResource(inputStream);
  }

  /**
   * MultipartFile 이미지를 입력받아 AWS S3 에 업로드 합니다.
   * @param multipartFile MultiPartFile 형태의 이미지
   * @return
   */
  @Override
  public UploadFile storeFile(MultipartFile multipartFile) {
    String storeFileName = createStoreFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());

    try (InputStream inputStream = multipartFile.getInputStream()) {
      s3Client.putObject(new PutObjectRequest(bucketName, storeFileName, inputStream, objectMetadata));
    } catch (IOException e) {
      throw new FileUploadException(FAIL_TO_UPLOAD_FILE.getDescription());
    }

    return UploadFile.of(multipartFile.getOriginalFilename(), storeFileName);
  }

  /**
   * 이미지 형태의 Byte 배열을 입력받아 AWS S3 에 업로드 합니다.
   * originalFileName 은 "확장자"를 포합해야합니다.
   * @param imageBytes 이미지의 byte 배열
   * @param originalName 이미지의 원래 이름
   * @return
   */
  @Override
  public UploadFile storeFile(byte[] imageBytes, String originalName) {
    String storeFileName = createStoreFileName(originalName);
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(imageBytes.length);

    try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
      s3Client.putObject(new PutObjectRequest(bucketName, storeFileName, inputStream, objectMetadata));
    } catch (IOException e) {
      throw new FileUploadException(FAIL_TO_UPLOAD_FILE.getDescription());
    }

    return UploadFile.of(originalName, storeFileName);
  }

  @Override
  public void deleteFile(String filename) {
    s3Client.deleteObject(bucketName, filename);
  }
}
