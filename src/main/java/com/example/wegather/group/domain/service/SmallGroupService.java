package com.example.wegather.group.domain.service;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.auth.AuthenticationManagerImpl;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SmallGroupService {

  private final SmallGroupRepository smallGroupRepository;
  private final MemberRepository memberRepository;
  private final AuthenticationManagerImpl authManager;
  private final InterestRepository interestRepository;

  @Transactional
  public SmallGroup addSmallGroup(CreateSmallGroupRequest request, String username) {

    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new IllegalStateException(MEMBER_NOT_FOUND.getDescription()));

    return smallGroupRepository.save(SmallGroup.builder()
            .name(request.getGroupName())
            .description(request.getDescription())
            .leader(member)
            .address(Address.of(request.getStreetAddress(), request.getLongitude(),
                request.getLatitude()))
            .maxMemberCount(request.getMaxMemberCount())
        .build());
  }

  public SmallGroup getSmallGroup(Long id) {
    return smallGroupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  public Page<SmallGroup> searchSmallGroups(SmallGroupSearchCondition cond, Pageable pageable) {
    return smallGroupRepository.search(cond, pageable);
  }

  @Transactional
  public void editSmallGroup(Long id, UpdateSmallGroupRequest request) {
    SmallGroup smallGroup = getSmallGroup(id);

    validateUpdatable(smallGroup, DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP.getDescription());

    smallGroup.updateSmallGroupInfo(
        request.getGroupName(),
        request.getDescription(),
        Address.of(request.getStreetAddress(), request.getLongitude(), request.getLatitude()),
        request.getMaxMemberCount());
  }

  private void validateUpdatable(SmallGroup smallGroup, String doNotHaveAuthorityToUpdateGroup) {
    MemberDetails principal = authManager.getPrincipal();

    if (!smallGroup.isLeader(principal.getMemberId()) && !principal.isAdmin()) {
      throw new AuthenticationException(doNotHaveAuthorityToUpdateGroup);
    }
  }

  @Transactional
  public void deleteSmallGroup(Long id) {
    SmallGroup smallGroup = getSmallGroup(id);

    validateUpdatable(smallGroup, DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP.getDescription());

    smallGroupRepository.deleteById(id);
  }

  @Transactional
  public void addSmallGroupInterest(Long smallGroupId, Long interestId) {
    SmallGroup smallGroup = findWithInterestById(smallGroupId);
    validateUpdatable(smallGroup, DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP.getDescription());
    Interest interest = findInterestById(interestId);

    smallGroup.addInterest(interest);
  }

  @Transactional
  public void removeSmallGroupInterest(Long smallGroupId, Long interestId) {
    SmallGroup smallGroup = findWithInterestById(smallGroupId);
    validateUpdatable(smallGroup, DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP.getDescription());
    Interest interest = findInterestById(interestId);

    smallGroup.removeInterest(interest);
  }

  private SmallGroup findWithInterestById(Long id) {
    return smallGroupRepository.findWithInterestById(id)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  private Interest findInterestById(Long interestId) {
    return interestRepository.findById(interestId)
        .orElseThrow(() -> new IllegalArgumentException(INTEREST_NOT_FOUND.getDescription()));
  }
}
