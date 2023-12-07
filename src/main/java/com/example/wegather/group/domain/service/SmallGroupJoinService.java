package com.example.wegather.group.domain.service;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.example.wegather.global.exception.customException.NoPermissionException;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.entity.SmallGroupJoin;
import com.example.wegather.group.domain.entity.SmallGroupMember;
import com.example.wegather.group.domain.repotitory.SmallGroupJoinRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupMemberRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.domain.vo.RecruitingProcess;
import com.example.wegather.group.dto.GroupJoinRequestDto;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SmallGroupJoinService {
  private final SmallGroupRepository smallGroupRepository;
  private final SmallGroupJoinRepository smallGroupJoinRepository;
  private final MemberRepository memberRepository;
  private final SmallGroupMemberRepository smallGroupMemberRepository;

  /**
   * 가입 요청
   *
   * @param smallGroupId 가입할 소모임 ID
   * @param loginId 로그인한 회원의 ID
   * @throws IllegalArgumentException
   *    - 이미 가입 요청한 회원일 경우
   * @throws IllegalStateException
   *    - 최대 회원수를 초과한 경우
   */
  @Transactional
  public void joinSmallGroup(Long smallGroupId, Long loginId) {
    SmallGroup smallGroup = findSmallGroupById(smallGroupId);
    Member member = findMemberById(loginId);

    validJoinSmallGroup(smallGroup, member);

    if (smallGroup.getRecruitingProcess() == RecruitingProcess.FCFS) {
      joinAsFCFS(smallGroup, member);
      return;
    }
    smallGroupJoinRepository.save(SmallGroupJoin.of(smallGroup, member));
  }

  private void validJoinSmallGroup(SmallGroup smallGroup, Member member) {
    if (smallGroupJoinRepository.existsBySmallGroupAndMember(smallGroup, member)) {
      throw new IllegalArgumentException(ALREADY_REQUEST_JOIN_MEMBER.getDescription());
    }
    if (smallGroupMemberRepository.existsBySmallGroupAndMember(smallGroup, member)) {
      throw new IllegalArgumentException(ALREADY_JOINED_MEMBER.getDescription());
    }
    validateExceedMaxCount(smallGroup);
  }

  /**
   * 선착순인 소모임에 가입합니다.
   * 가입 테이블 데이터 추가
   * 소모임 회원에 바로 추가
   * @param smallGroup
   * @param member
   */
  private void joinAsFCFS(SmallGroup smallGroup, Member member) {
    smallGroupJoinRepository.save(SmallGroupJoin.ofAsFCFS(smallGroup, member));
    smallGroupMemberRepository.save(SmallGroupMember.of(smallGroup, member));
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
      throw new NoPermissionException(LEADER_ONLY.getDescription());
    }
  }

  /**
   * 소모임 가입 요청 승인
   * 소모임 멤버에 추가됩니다.
   * @param id       소모임 ID
   * @param requestId 가입 요청 ID
   * @param loginId  로그인한 회원의 ID
   * @throws NoPermissionException
   *    - 소모임장이 아닌 경우
   * @throws IllegalStateException
   *    - 최대 회원수를 초과한 경우
   */
  @Transactional
  public void approveJoinRequest(Long id, Long requestId, Long loginId) {
    SmallGroup smallGroup = findSmallGroupById(id);
    validateIsLeader(smallGroup, loginId);
    // 소모임 가입 승인
    SmallGroupJoin smallGroupJoin = findSmallGroupJoinById(requestId);
    smallGroupJoin.approve();
    // 소모임 멤버 추가
    addSmallGroupMember(smallGroup, smallGroupJoin);
  }

  private void addSmallGroupMember(SmallGroup smallGroup, SmallGroupJoin smallGroupJoin) {
    // 회원수 체크
    validateExceedMaxCount(smallGroup);
    // 소모임 회원 추가
    smallGroupMemberRepository.save(SmallGroupMember.of(smallGroup, smallGroupJoin.getMember()));
  }

  // 최대 회원수를 넘는지 체크합니다.
  private void validateExceedMaxCount(SmallGroup smallGroup) {
    Long nowMemberCount = smallGroupMemberRepository.countBySmallGroup(smallGroup);
    if (smallGroup.isExceedMaxMember(nowMemberCount)) {
      throw new IllegalStateException(EXCESS_MAX_MEMBER_COUNT.getDescription());
    }
  }

  /**
   * 소모임 가입 요청 거절
   *
   * @param id       소모임 ID
   * @param requestId 가입 요청 ID
   * @param loginId  로그인한 회원의 ID
   * @throws NoPermissionException - 소모임장이 아닌 경우
   */
  @Transactional
  public void rejectJoinRequest(Long id, Long requestId, Long loginId) {
    SmallGroup smallGroup = findSmallGroupById(id);
    validateIsLeader(smallGroup, loginId);

    SmallGroupJoin smallGroupJoin = findSmallGroupJoinById(requestId);
    smallGroupJoin.reject();
  }

  private SmallGroupJoin findSmallGroupJoinById(Long requestId) {
    return smallGroupJoinRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_JOIN_NOT_FOUND.getDescription()));
  }

  private void validateIsLeader(SmallGroup smallGroup, Long loginId) {
    if (!smallGroup.isLeader(loginId)) {
      throw new NoPermissionException(LEADER_ONLY.getDescription());
    }
  }

  private SmallGroup findSmallGroupById(Long id) {
    return smallGroupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  private Member findMemberById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException(MEMBER_NOT_FOUND.getDescription()));
  }
}
