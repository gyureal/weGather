package com.example.wegather.group.domain.service;

import com.example.wegather.global.customException.NoPermissionException;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.group.domain.repotitory.SmallGroupJoinRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.GroupJoinRequestDto;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SmallGroupJoinService {
  private final SmallGroupRepository smallGroupRepository;
  private final SmallGroupJoinRepository smallGroupJoinRepository;
  private final MemberRepository memberRepository;

  /**
   * 가입 요청 유효성 체크
   * @param smallGroupId 가입할 소모임 ID
   * @param loginId 로그인한 회원의 ID
   * @throws IllegalArgumentException
   *    - 이미 가입 요청한 회원일 경우
   *    - 소모임장인 경우
   */
  public void requestJoin(Long smallGroupId, Long loginId) {
    SmallGroup smallGroup = findSmallGroupById(smallGroupId);
    Member member = findMemberById(loginId);

    validRequestJoin(smallGroup, member);

    smallGroupJoinRepository.save(SmallGroupJoin.of(smallGroup, member));
  }

  private void validRequestJoin(SmallGroup smallGroup, Member member) {
    if (smallGroupJoinRepository.existsBySmallGroupAndMember(smallGroup, member)) {
      throw new IllegalArgumentException("이미 가입 요청한 회원입니다.");
    }
    if (smallGroup.isLeader(member.getId())) {
      throw new IllegalArgumentException("소모임장은 가입 요청할 수 없습니다.");
    }
  }

  /**
   *
   * @param id 조회할 소모임 ID
   * @param loginId 로그인한 회원의 ID
   * @param pageable 페이징 파라메터
   * @throws NoPermissionException
   *    - 요청자가 소모임장이 아닌 경우
   * @return
   */
  public Page<GroupJoinRequestDto> getAllJoinRequests(Long id, Long loginId ,Pageable pageable) {
    SmallGroup smallGroup = findSmallGroupById(id);

    validateGetAllJoinRequests(smallGroup, loginId);

    return smallGroupJoinRepository.findRequestBySmallGroup(smallGroup, pageable)
        .map(GroupJoinRequestDto::from);
  }

  private void validateGetAllJoinRequests(SmallGroup smallGroup, Long loginId) {
    if (!smallGroup.isLeader(loginId)) {
      throw new NoPermissionException("소모임장만 조회 가능합니다.");
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
