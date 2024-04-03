package com.example.wegather.global.upload;

import com.example.wegather.auth.MemberDetails;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ImageUploadController {
  private final ImageUploadService imageUploadService;

  @GetMapping("/images/{filename}")
  public ResponseEntity<Resource> downloadImage(@PathVariable String filename) {
    return ResponseEntity.ok(imageUploadService.downloadImage(filename));
  }

  @PostMapping("/images")
  public ResponseEntity<Void> uploadImage(@RequestParam("image") MultipartFile multipartFile,
                                          @AuthenticationPrincipal MemberDetails memberDetails) {
    String storedFileName = imageUploadService.uploadImage(multipartFile, memberDetails.getMemberId());
    return ResponseEntity.created(URI.create("/images/" + storedFileName)).build();
  }

  @DeleteMapping("/images/{filename}")
  public ResponseEntity<Void> deleteImage(@PathVariable String filename) {
    imageUploadService.deleteImage(filename);
    return ResponseEntity.noContent().build();
  }
}
