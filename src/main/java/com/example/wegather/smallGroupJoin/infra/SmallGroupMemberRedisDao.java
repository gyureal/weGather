package com.example.wegather.smallGroupJoin.infra;

import com.example.wegather.smallGroupJoin.domin.repository.SmallGroupMemberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SmallGroupMemberRedisDao implements SmallGroupMemberRedisRepository {
  private static final String ENTITY = "smallGroup";
  private static final String COUNT = "count";
  private static final String EXCESS_MAX_MEMBER_COUNT = "최대 가입 회원수를 초과하였습니다.";

  private final StringRedisTemplate redisTemplate;

  /**
   * 해당 소모임에 회원을 추가합니다.
   * 최대 회원수를 넘지 않도록 합니다.
   * @param smallGroupId 소모임의 id
   * @param maxCount 소모임의 최대 가입 인원수
   * @throws IllegalStateException
   *     최대 가입 인원수를 초과한 경우
   * @return 추가 성공 여부
   */
  @Override
  public void addMemberInSmallGroup(Long smallGroupId, Integer maxCount) {
    ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

    String countKey = generateCountKey(smallGroupId);
      // 가입 인원수 증가
    Long increasedCount = valueOps.increment(countKey);

    if (increasedCount > maxCount) {
      valueOps.decrement(countKey);
      throw new IllegalStateException(EXCESS_MAX_MEMBER_COUNT);
    }
  }

  /**
   * 키 생성
   * ex) smallGroup:1:count - 소모임에 가입된 회원의 수
   * @param smallGroupId
   * @return
   */
  private String generateCountKey(Long smallGroupId) {
    return ENTITY + ":" + smallGroupId + ":" + COUNT;
  }
}
