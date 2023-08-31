package com.example.wegather.group.domain;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.auth.AuthenticationManagerImpl;
import com.example.wegather.global.customException.AuthenticationException;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.group.domain.vo.MaxMemberCount;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.dto.InterestDto;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SmallGroupService {

  private static final String GROUP_NOT_FOUND = "소모임을 찾을 수 없습니다.";
  private static final String USERNAME_IN_AUTH_NOT_FOUND = "인증정보에 있는 회원정보를 찾을 수 없습니다.";
  private static final String DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP = "소모임 정보를 수정할 권한이 없습니다.";
  private static final String DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP = "소모임을 삭제할 권한이 없습니다.";
  public static final String INTEREST_NOT_FOUND = "관심사를 찾을 수 없습니다.";
  private final SmallGroupRepository groupRepository;
  private final MemberRepository memberRepository;
  private final AuthenticationManagerImpl authManager;
  private final InterestRepository interestRepository;

  @Transactional
  public SmallGroup addGroup(CreateSmallGroupRequest request, String username) {

    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new IllegalStateException(USERNAME_IN_AUTH_NOT_FOUND));

    return groupRepository.save(SmallGroup.builder()
            .name(request.getGroupName())
            .description(request.getDescription())
            .leader(member)
            .address(Address.of(request.getStreetAddress(), request.getLongitude(),
                request.getLatitude()))
            .maxMemberCount(MaxMemberCount.of(request.getMaxMemberCount()))
        .build());
  }

  public SmallGroup getGroup(Long id) {
    return groupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(GROUP_NOT_FOUND));
  }

  public Page<SmallGroup> searchGroups(SmallGroupSearchCondition cond, Pageable pageable) {
    return groupRepository.search(cond, pageable);
  }

  @Transactional
  public void editGroup(Long id, UpdateSmallGroupRequest request) {
    SmallGroup smallGroup = getGroup(id);

    MemberDetails principal = authManager.getPrincipal();

    if (!smallGroup.isLeader(principal.getUsername()) && !principal.isAdmin()) {
      throw new AuthenticationException(DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP);
    }
    
    smallGroup.updateGroupTotalInfo(
        request.getGroupName(),
        request.getDescription(),
        Address.of(request.getStreetAddress(), request.getLongitude(), request.getLatitude()),
        MaxMemberCount.of(request.getMaxMemberCount()));
  }

  @Transactional
  public void deleteGroup(Long id) {
    SmallGroup smallGroup = getGroup(id);

    MemberDetails principal = authManager.getPrincipal();

    if (!smallGroup.isLeader(principal.getUsername()) && !principal.isAdmin()) {
      throw new AuthenticationException(DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP);
    }

    groupRepository.deleteById(id);
  }

  @Transactional
  public void addSmallGroupInterest(Long smallGroupId, Long interestId) {
    SmallGroup smallGroup = getGroup(smallGroupId);
    Interest interest = findInterestById(interestId);

    smallGroup.addInterest(interest);
  }

  @Transactional
  public void removeSmallGroupInterest(Long smallGroupId, Long interestId) {
    SmallGroup smallGroup = getGroup(smallGroupId);
    Interest interest = findInterestById(interestId);

    smallGroup.removeInterest(interest);
  }

  private Interest findInterestById(Long interestId) {
    return interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException(INTEREST_NOT_FOUND));
  }
}
