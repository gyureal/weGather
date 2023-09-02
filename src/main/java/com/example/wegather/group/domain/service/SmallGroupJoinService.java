package com.example.wegather.group.domain.service;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.group.domain.repotitory.SmallGroupJoinRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SmallGroupJoinService {
  private final SmallGroupRepository smallGroupRepository;
  private final SmallGroupJoinRepository smallGroupJoinRepository;
  private final MemberRepository memberRepository;

  public void requestJoin(Long smallGroupId, Long memberId) {
    SmallGroup smallGroup = findSmallGroupById(smallGroupId);
    Member member = findMemberById(memberId);

    validRequestJoin(smallGroup, member);

    smallGroupJoinRepository.save(SmallGroupJoin.of(smallGroup, member));
  }

  /**
   * 가입 요청 유효성 체크
   * @param smallGroup
   * @param member
   * @throws IllegalArgumentException
   *    - 이미 가입 요청한 회원일 경우
   *    - 소모임장인 경우
   */
  private void validRequestJoin(SmallGroup smallGroup, Member member) {
    if (smallGroupJoinRepository.existsBySmallGroupAndMember(smallGroup, member)) {
      throw new IllegalArgumentException("이미 가입 요청한 회원입니다.");
    }
    if (smallGroup.isLeader(member.getId())) {
      throw new IllegalArgumentException("소모임장은 가입 요청할 수 없습니다.");
    }
  }

  private SmallGroup findSmallGroupById(Long id) {
    return smallGroupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("소모임을 찾을 수 없습니다."));
  }

  private Member findMemberById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("로그인한 회원 정보를 찾을 수 없습니다."));
  }
}
