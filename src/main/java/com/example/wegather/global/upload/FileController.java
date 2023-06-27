package com.example.wegather.global.upload;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FileController {
  private final StoreFile storeFile;

  @GetMapping("/images/{filename}")
  public ResponseEntity<Resource> downloadImage(@PathVariable String filename) {
    return ResponseEntity.ok(storeFile.getFile(filename));
  }

  @PostMapping("/images")
  public ResponseEntity<Void> uploadImage(@RequestParam("file") MultipartFile file) {
    UploadFile uploadFile = storeFile.storeFile(file);
    return ResponseEntity.created(URI.create("/images/" + uploadFile.getStoreFileName())).build();
  }

  @DeleteMapping("/images/{filename}")
  public ResponseEntity<Void> deleteImage(@PathVariable String filename) {
    storeFile.deleteFile(filename);
    return ResponseEntity.noContent().build();
  }
}
