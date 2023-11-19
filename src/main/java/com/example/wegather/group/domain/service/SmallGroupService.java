package com.example.wegather.group.domain.service;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.exception.customException.AuthenticationException;
import com.example.wegather.global.exception.customException.NoPermissionException;
import com.example.wegather.global.upload.StoreFile;
import com.example.wegather.global.upload.UploadFile;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.ManagerAndMemberDto;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateBannerRequest;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import com.example.wegather.groupJoin.domain.entity.SmallGroupMember;
import com.example.wegather.groupJoin.domain.repository.SmallGroupMemberRepository;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.domain.InterestService;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SmallGroupService {

  private final SmallGroupRepository smallGroupRepository;
  private final MemberRepository memberRepository;
  private final InterestRepository interestRepository;
  private final InterestService interestService;
  private final SmallGroupMemberRepository smallGroupMemberRepository;
  private final StoreFile storeFile;

  @Transactional
  public SmallGroup addSmallGroup(CreateSmallGroupRequest request, Long memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalStateException(MEMBER_NOT_FOUND.getDescription()));

    SmallGroup savedGroup = smallGroupRepository.save(SmallGroup.builder()
        .path(request.getPath())
        .name(request.getName())
        .shortDescription(request.getShortDescription())
        .fullDescription(request.getFullDescription())
        .maxMemberCount(request.getMaxMemberCount())
        .leader(member)
        .build());

    saveLeaderAsManager(member, savedGroup);
    return savedGroup;
  }

  /**
   * 소모임 리더(생성자)를 소모임의 관리자로 추가합니다.
   * @param leader 소모임 생성자
   * @param savedGroup 생성된 소모임
   */
  private void saveLeaderAsManager(Member leader, SmallGroup savedGroup) {
    SmallGroupMember smallGroupMember = SmallGroupMember.of(savedGroup, leader);
    smallGroupMember.changeTypeManager();
    smallGroupMemberRepository.save(smallGroupMember);
  }

  public SmallGroup getSmallGroup(Long id) {
    return smallGroupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  public SmallGroupDto getSmallGroupByPath(String path, MemberDetails memberDetails) {
    SmallGroup smallGroup = findSmallGroupByPath(path);

    SmallGroupDto smallGroupDto = SmallGroupDto.from(smallGroup);

    boolean isMember = smallGroup.isMember(memberDetails.getMemberId());
    boolean isJoinalble = smallGroup.isJoinable(memberDetails.getMemberId(), isMember);
    smallGroupDto.changeMemberOrManager(isMember);
    smallGroupDto.changeJoinable(isJoinalble);

    return smallGroupDto;
  }

  private SmallGroup findSmallGroupByPath(String path) {
    return smallGroupRepository.findByPath(path)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  public Page<SmallGroup> searchSmallGroups(SmallGroupSearchCondition cond, Pageable pageable) {
    return smallGroupRepository.search(cond, pageable);
  }

  @Transactional
  public void editSmallGroup(MemberDetails principal, Long id, UpdateSmallGroupRequest request) {
    SmallGroup smallGroup = getSmallGroup(id);

    validateUpdatable(principal, smallGroup);

    smallGroup.updateSmallGroupInfo(
        request.getGroupName(),
        request.getShortDescription(),
        request.getFullDescription(),
        Address.of(request.getStreetAddress(), request.getLongitude(), request.getLatitude()),
        request.getMaxMemberCount());
  }

  private void validateUpdatable(MemberDetails principal, SmallGroup smallGroup) {

    if (!smallGroup.isLeader(principal.getMemberId())) {
      throw new NoPermissionException(PERMISSION_DENIED.getDescription());
    }
  }

  @Transactional
  public void deleteSmallGroup(MemberDetails principal, Long id) {
    SmallGroup smallGroup = getSmallGroup(id);

    validateUpdatable(principal, smallGroup);

    smallGroupRepository.deleteById(id);
  }

  @Transactional
  public void addSmallGroupInterest(MemberDetails principal, String smallGroupPath, String interestName) {
    SmallGroup smallGroup = findSmallGroupByPath(smallGroupPath);
    validateUpdatable(principal, smallGroup);

    Interest interest = interestService.findOrAddInterestByName(interestName);

    smallGroup.addInterest(interest);
  }

  @Transactional
  public void removeSmallGroupInterest(MemberDetails principal, String smallGroupPath, String interestName) {
    SmallGroup smallGroup = findSmallGroupByPath(smallGroupPath);
    validateUpdatable(principal, smallGroup);
    Interest interest = interestRepository.findByName(interestName)
        .orElseThrow(() -> new IllegalArgumentException(INTEREST_NOT_FOUND.getDescription()));

    smallGroup.removeInterest(interest);
  }

  public List<ManagerAndMemberDto> getSmallGroupManagersAndMembers(String path) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    return smallGroupMemberRepository.findBySmallGroupOrderbyType(smallGroup).stream()
        .map(ManagerAndMemberDto::from).collect(Collectors.toList());
  }

  @Transactional
  public void updateBanner(MemberDetails memberDetails, String path,
      UpdateBannerRequest request) {

    SmallGroup smallGroup = findSmallGroupByPath(path);
    List<SmallGroupMember> managers = smallGroupMemberRepository.findManagerBySmallGroupId(
        smallGroup.getId());

    boolean exists = false;
    for(SmallGroupMember manager : managers) {
      if (Objects.equals(manager.getMember().getId(), memberDetails.getMemberId())) {
        exists = true;
        break;
      }
    }

    if(!exists) {
      throw new NoPermissionException(PERMISSION_DENIED.getDescription());
    }

    // 이미지 업로드
    byte[] imageBytes = storeFile.decodeBase64Image(request.getImage());
    UploadFile uploadFile = storeFile.storeFile(imageBytes, request.getOriginalImageName());

    String originalImage = smallGroup.getBanner();
    smallGroup.updateBanner(uploadFile.getStoreFileName());

    // 기존 이미지 삭제
    if (StringUtils.hasText(originalImage)) {
      storeFile.deleteFile(originalImage);
    }
  }

  public List<String> getInterests(String path) {
    SmallGroup smallGroupByInterests = findWithInterestByPath(path);
    return smallGroupByInterests.getSmallGroupInterests().stream()
        .map(smallGroupInterest -> smallGroupInterest.getInterest().getName())
        .collect(Collectors.toList());
  }

  private SmallGroup findWithInterestByPath(String path) {
    return smallGroupRepository.findWithInterestByPath(path)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }
}
