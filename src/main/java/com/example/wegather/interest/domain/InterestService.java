package com.example.wegather.interest.domain;

import static com.example.wegather.global.Message.Error.*;

import com.example.wegather.interest.dto.CreateInterestRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;

  /**
   * 관심사를 새로 추가합니다.
   * @param request 관심사명
   * @return 생성된 관심사
   * @throws IllegalArgumentException 관심사 이름이 이미 존재하는 경우 예외를 던집니다.
   */
  public Interest addInterest(CreateInterestRequest request) {

    boolean isNameExists = interestRepository.existsByName(request.getInterestName());
    if (isNameExists) {
      throw new IllegalArgumentException(INTEREST_NAME_ALREADY_EXISTS);
    }
    return interestRepository.save(Interest.of(request.getInterestName()));
  }

  /**
   * 전체 관심사를 조회합니다.
   * @return
   */
  public List<Interest> getAllInterests() {
    return interestRepository.findAll();
  }

  /**
   * id에 해당하는 관심사를 조회합니다.
   * @param id
   * @return
   * @throws IllegalArgumentException id에 해당하는 관심사가 없는 경우 예외를 던집니다.
   */
  public Interest getInterest(Long id) {
    return interestRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(INTEREST_NOT_FOUND));
  }

  /**
   * id에 해당하는 관심사를 삭제합니다.
   * @param id
   */
  public void deleteInterest(Long id) {
    interestRepository.deleteById(id);
  }
}
