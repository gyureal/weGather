package com.example.wegather.interest;

import com.example.wegather.interest.dto.CreateInterestRequest;
import com.example.wegather.interest.dto.InterestDto;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/interests")
@RestController
public class InterestController {
  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> createInterest(@Valid @RequestBody CreateInterestRequest request) {
    InterestDto interestDto = InterestDto.from(interestService.addInterest(request));
    return ResponseEntity.created(URI.create("/interests/" + interestDto.getId()))
        .body(interestDto);
  }
}
