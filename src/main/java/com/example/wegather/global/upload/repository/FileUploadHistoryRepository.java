package com.example.wegather.global.upload.repository;

import com.example.wegather.global.upload.entity.FileUploadHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadHistoryRepository extends JpaRepository<FileUploadHistory, Long> {
  Optional<FileUploadHistory> findByStoredFileName(String storedFileName);
}
