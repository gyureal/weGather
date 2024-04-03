package com.example.wegather.global.upload.entity;

import com.example.wegather.global.BaseTimeEntity;
import com.example.wegather.member.domain.entity.Member;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FileUploadHistory extends BaseTimeEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
  private String storedFileName;
  private String originalFileName;
  @Enumerated(EnumType.STRING)
  private StorageType storageType;
  @Enumerated(EnumType.STRING)
  private FileUploadStatus fileUploadStatus;

  public void deleted() {
    fileUploadStatus = FileUploadStatus.DELETED;
  }
}
