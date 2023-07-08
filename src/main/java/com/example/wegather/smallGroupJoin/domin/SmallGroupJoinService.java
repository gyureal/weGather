package com.example.wegather.smallGroupJoin.domin;

import com.example.wegather.global.auth.AuthenticationManager;
import com.example.wegather.group.domain.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import com.example.wegather.smallGroupJoin.domin.repository.SmallGroupMemberRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SmallGroupJoinService {

  private static final String SMALL_GROUP_NOT_FOUND_TO_JOIN = "가입할 소모임을 찾을 수 없습니다.";
  private static final String MEMBER_NOT_FOUND_TO_JOIN = "가입할 회원을 찾을 수 없습니다.";
  private static final String ALREADY_JOINED_MEMBER = "이미 가입한 회원입니다.";
  private final MemberRepository memberRepository;
  private final SmallGroupRepository smallGroupRepository;
  private final SmallGroupMemberRepository smallGroupMemberRepository;
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
   */
  public void joinGroup(Long smallGroupId) {
    SmallGroup smallGroup = smallGroupRepository.findById(smallGroupId)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND_TO_JOIN));

    String username = authManager.getUsername();
    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_TO_JOIN));

    if(smallGroupMemberRepository.existsBySmallGroup_IdAndMember_Id(smallGroupId, member.getId())) {
      throw new IllegalArgumentException(ALREADY_JOINED_MEMBER);
    }

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
   * @return
   */
  public Page<SmallGroupMember> readJoinMember(Long groupId, Optional<MemberStatus> status, Pageable pageable) {
    return smallGroupMemberRepository.search(groupId, status, pageable);
  }
}
