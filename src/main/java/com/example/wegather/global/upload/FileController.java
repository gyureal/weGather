package com.example.wegather.global.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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

  @ResponseBody
  @GetMapping("/images/{filename}")
  public Resource downloadImage(@PathVariable String filename) {
    return storeFile.getFile(filename);
  }

  @ResponseBody
  @PostMapping("/images")
  public void uploadImage(@RequestParam("file") MultipartFile file) {
    storeFile.storeFile(file);
  }
}
