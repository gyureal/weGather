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
  private static final String USERNAME = "username";
  private static final String COUNT = "count";
  private static final String ALREADY_JOINED_MEMBER = "이미 가입한 회원입니다.";
  private static final String EXCESS_MAX_MEMBER_COUNT = "최대 가입 회원수를 초과하였습니다.";

  private final StringRedisTemplate redisTemplate;

  /**
   * 해당 소모임에 회원을 추가합니다.
   * 최대 회원수를 넘지 않도록 합니다.
   * @param smallGroupId 소모임의 id
   * @param username 가입 시도한 화원의 아이디
   * @param maxCount 소모임의 최대 가입 인원수
   * @throws IllegalStateException
   *     최대 가입 인원수를 초과한 경우
   * @return 추가 성공 여부
   */
  @Override
  public void addMemberInSmallGroup(Long smallGroupId, String username ,Integer maxCount) {
    ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

    String memberKey = generateMemberKey(smallGroupId, username);
    boolean isMemberAdded = valueOps.setIfAbsent(memberKey, username);  // 회원 추가

    String countKey = generateCountKey(smallGroupId);
    if (isMemberAdded) {
      // 가입 인원수 증가
      valueOps.increment(countKey);

      // 가입 인원수 체크
      Integer memberCount = Integer.parseInt(valueOps.get(countKey));
      if (memberCount > maxCount) {
        // 가입 거부
        valueOps.decrement(countKey);
        redisTemplate.delete(memberKey);    // 가입한 회원 제거
        throw new IllegalStateException(EXCESS_MAX_MEMBER_COUNT);
      }
      return;
    }
    // 이미 가입한 사용자인 경우
    throw new IllegalArgumentException(ALREADY_JOINED_MEMBER);
  }

  /**
   * 키 생성
   * ex) smallGroup:1:username:asdf1234 - 소모임에 가입된 회원 username
   * @param smallGroupId
   * @param username
   * @return
   */
  private String generateMemberKey(Long smallGroupId, String username) {
    return ENTITY + ":" + smallGroupId + ":" + USERNAME + ":" + username;
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
