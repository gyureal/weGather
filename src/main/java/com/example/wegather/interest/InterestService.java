package com.example.wegather.interest;

import com.example.wegather.interest.dto.CreateInterestRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private static final String INTEREST_NAME_ALREADY_EXISTS = "관심사 이름이 이미 존재합니다.";
  private final InterestRepository interestRepository;

  /**
   * 관심사를 새로 추가합니다.
   * @throws IllegalArgumentException 관심사 이름이 이미 존재하는 경우 예외를 던집니다.
   * @param request 관심사명
   * @return 생성된 관심사
   */
  public Interest addInterest(CreateInterestRequest request) {

    boolean isNameExists = interestRepository.existsByName(request.getInterestName());
    if (isNameExists) {
      throw new IllegalArgumentException(INTEREST_NAME_ALREADY_EXISTS);
    }
    return interestRepository.save(Interest.of(request.getInterestName()));
  }
}
