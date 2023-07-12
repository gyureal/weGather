package com.example.wegather.smallGroupJoin.domin;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.auth.AuthenticationManager;
import com.example.wegather.global.customException.AuthenticationException;
import com.example.wegather.group.domain.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.smallGroupJoin.domin.repository.SmallGroupMemberRedisRepository;
import com.example.wegather.smallGroupJoin.domin.repository.SmallGroupMemberRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SmallGroupJoinService {

  private static final String SMALL_GROUP_NOT_FOUND = "소모임을 찾을 수 없습니다.";
  private static final String MEMBER_NOT_FOUND_TO_JOIN = "가입할 회원을 찾을 수 없습니다.";
  private static final String ALREADY_JOINED_MEMBER = "이미 가입한 회원입니다.";
  private static final String MEMBER_NOT_FOUND_TO_LEAVE = "탈퇴 처리할 회원이 존재하지 않습니다.";
  private static final String LEAVE_CAN_ONLY_LEADER_ADMIN_SELF = "탈퇴는 모임장 이나 관리자 또는 회원 본인만 가능합니다.";
  private static final String MEMBER_NOT_JOINED_IN_GROUP = "소모임에 해당 회원이 가입되어 있지 않습니다.";
  private final MemberRepository memberRepository;
  private final SmallGroupRepository smallGroupRepository;
  private final SmallGroupMemberRepository smallGroupMemberRepository;
  private final SmallGroupMemberRedisRepository redisRepository;
  private final AuthenticationManager authManager;
  private final Clock clock;

  /**
   * 소모임에 가입 합니다.
   * 요청과 동시에 승인 됩니다.
   * @param smallGroupId 소그룹 아이디
   * @throws IllegalArgumentException
   *     소모임 정보를 찾지 못했을 때
   *     회원 정보를 찾지 못했을 때
   *     이미 가입한 회원일 때
   * @throws IllegalStateException
   *    가입할 수 있는 인원수를 초과한 경우
   */
  @Transactional
  public void joinGroup(Long smallGroupId) {
    SmallGroup smallGroup = smallGroupRepository.findById(smallGroupId)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND));

    String username = authManager.getUsername();
    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_TO_JOIN));

    if(smallGroupMemberRepository.existsBySmallGroup_IdAndMember_Id(smallGroupId, member.getId())) {
      throw new IllegalArgumentException(ALREADY_JOINED_MEMBER);
    }

    Integer maxMemberCount = smallGroup.getMaxMemberCount().getValue();
    redisRepository.addMemberInSmallGroup(smallGroupId, username, maxMemberCount);

    smallGroupMemberRepository.save(SmallGroupMember
        .builder()
        .smallGroup(smallGroup)
        .member(member)
        .status(MemberStatus.APPROVED)
        .registeredDatetime(LocalDateTime.now(clock))
        .build());
  }

  /**
   * 가입요청한 회원을 조회합니다.
   * @param groupId 소모임 Id
   * @param status 가입요청 상태
   * @param pageable 페이징 인수
   * @throws IllegalArgumentException 소모임을 찾을 수 없을 때
   * @throws AuthenticationException 소모임장이나 관리자가 아닐때
   * @return
   */
  public Page<SmallGroupMember> readJoinMember(Long groupId, Optional<MemberStatus> status, Pageable pageable) {
    MemberDetails principal = authManager.getPrincipal();

    SmallGroup smallGroup = smallGroupRepository.findById(groupId)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND));

    if (!smallGroup.isLeader(principal.getUsername()) && !principal.isAdmin()) {
      throw new AuthenticationException();
    }

    return smallGroupMemberRepository.search(groupId, status, pageable);
  }

  /**
   * 회원을 탈퇴 처리 합니다.
   * @param smallGroupId  탈퇴시킬 소그룹 Id
   * @param memberIdToLeave  탈퇴처리할 회원 Id
   * @throws IllegalArgumentException
   *     탈퇴시킬 소그룹을 찾을 수 없을 때
   *     탈퇴처리할 회원을 찾을 수 없을 때
   *     탈퇴시킬 소그룹에 회원이 가입되어 있지 않을 때
   * @throws AuthenticationException
   *     소모임장, 관리자, 회원 본인이 아닌 경우
   */
  @Transactional
  public void leave(Long smallGroupId, Long memberIdToLeave) {
    SmallGroup smallGroup = smallGroupRepository.findById(smallGroupId)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND));
    Member memberToDelte = memberRepository.findById(memberIdToLeave)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_TO_LEAVE));

    MemberDetails principal = authManager.getPrincipal();
    String myUsername =principal.getUsername();
    if (!smallGroup.isLeader(myUsername) && !principal.isAdmin()
        && !isAmIDeleteMember(myUsername, memberToDelte)) {
      throw new AuthenticationException(LEAVE_CAN_ONLY_LEADER_ADMIN_SELF);
    }

    SmallGroupMember smallGroupMember = smallGroupMemberRepository
        .findBySmallGroup_IdAndMember_Id(smallGroupId, memberIdToLeave)
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_JOINED_IN_GROUP));
    if(smallGroupMember.getStatus() != MemberStatus.APPROVED) {
      throw new IllegalArgumentException(MEMBER_NOT_JOINED_IN_GROUP);
    }

    smallGroupMember.changeStatus(MemberStatus.LEAVE);
  }

  private boolean isAmIDeleteMember(String memberUsername, Member memberToDelete) {
    if (memberUsername.equals(memberToDelete.getUsername().getValue())) {
      return true;
    }
    return false;
  }
}
