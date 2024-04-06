package com.example.wegather.interest.web;

import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestService;
import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/interests")
@RestController
public class InterestController {
  private final InterestService interestService;

  /**
   * 관심사를 새로 추가합니다.
   * @param request 관심사명
   * @return 생성된 관심사
   * @throws IllegalArgumentException 관심사 이름이 이미 존재하는 경우 예외를 던집니다.
   */
  @PostMapping
  public ResponseEntity<InterestDto> createInterest(@Valid @RequestBody CreateInterestRequest request) {
    InterestDto interestDto = InterestDto.from(interestService.addInterest(request));
    return ResponseEntity.created(URI.create("/interests/" + interestDto.getId()))
        .body(interestDto);
  }

  /**
   * 전체 관심사를 조회합니다.
   * @return 전체 관심사 목록
   */
  @GetMapping
  public ResponseEntity<List<InterestDto>> readAllInterests() {
    List<InterestDto> collect = interestService.getAllInterests();
    return ResponseEntity.ok(collect);
  }

  /**
   * 관심사 화이트 리스트를 조회합니다.
   * @return 전체 관심사 목록
   */
  @GetMapping("/whitelist")
  public ResponseEntity<List<String>> readWhitelist() {
    return ResponseEntity.ok(interestService.getInterestWhiteList());
  }

  /**
   * id로 관심사를 조회합니다.
   * @param id
   * @return
   * @throws IllegalArgumentException id에 해당하는 관심사가 없는 경우 예외를 던집니다.
   */
  @GetMapping("/{id}")
  public ResponseEntity<InterestDto> readInterestById(@PathVariable Long id) {
    return ResponseEntity.ok(InterestDto.from(interestService.findInterestById(id)));
  }

  /**
   * id로 관심사를 삭제합니다.
   * @param id
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInterestById(@PathVariable Long id) {
    interestService.deleteInterest(id);
    return ResponseEntity.noContent().build();
  }
}
