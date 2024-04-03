package com.example.wegather.group.domain.service;

import static com.example.wegather.global.exception.ErrorCode.*;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.exception.customException.NoPermissionException;
import com.example.wegather.global.upload.ImageUploadService;
import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupJoinRepository;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.domain.vo.RecruitingType;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.ManagerAndMemberDto;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchDto;
import com.example.wegather.group.dto.UpdateBannerRequest;
import com.example.wegather.group.dto.UpdateGroupDescriptionRequest;
import com.example.wegather.group.dto.UpdateGroupWithMultipartImageRequest;
import com.example.wegather.group.domain.entity.SmallGroupMember;
import com.example.wegather.group.domain.repotitory.SmallGroupMemberRepository;
import com.example.wegather.interest.domain.Interest;
import com.example.wegather.interest.domain.InterestRepository;
import com.example.wegather.interest.domain.InterestService;
import com.example.wegather.member.domain.entity.Member;
import com.example.wegather.member.domain.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SmallGroupService {

  private final SmallGroupRepository smallGroupRepository;
  private final MemberRepository memberRepository;
  private final InterestRepository interestRepository;
  private final InterestService interestService;
  private final SmallGroupMemberRepository smallGroupMemberRepository;
  private final SmallGroupJoinRepository smallGroupJoinRepository;
  private final ImageUploadService imageUploadService;

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

    smallGroupDto.changeJoinable(smallGroup.isJoinable());
    smallGroupDto.changeMemberOrManager(smallGroup.isMemberOrManager(memberDetails.getMemberId()));
    smallGroupDto.changeJoinRequested(
        smallGroupJoinRepository.existsRequestedJoin(smallGroup.getId(), memberDetails.getMemberId()));
    return smallGroupDto;
  }

  private SmallGroup findSmallGroupByPath(String path) {
    return smallGroupRepository.findByPath(path)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  public Page<SmallGroupSearchDto> searchSmallGroups(String keyword, Pageable pageable) {
    Page<SmallGroup> searchResult = smallGroupRepository.search(keyword, pageable);
    return searchResult.map(SmallGroupSearchDto::from);
  }

  /**
   * 소모임 소개 정보를 수정합니다. (base64 형식의 썸네일 이미지)
   * @param principal 로그인 유저
   * @param path  소모임 path
   * @param request 소모임 소개 정보
   */
  @Transactional
  public void editSmallGroupDescription(MemberDetails principal, String path, UpdateGroupDescriptionRequest request) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(principal, smallGroup);

    if (StringUtils.hasText(request.getImage())) { // 이미지 값이 있을 때만 저장
      String storedImageName = imageUploadService.uploadImage(request.getImage(), request.getOriginalImageName());
      smallGroup.updateImage(storedImageName);
    }


    smallGroup.updateSmallGroupDescription(
        request.getShortDescription(),
        request.getFullDescription());
  }

  /**
   * 소모임 소개 정보를 수정합니다. (MultipartFile 형식의 썸네일 이미지)
   * @param principal 로그인 유저
   * @param path  소모임 path
   * @param descriptionInfo 소모임 소개 정보
   * @param image MultipartFile 형식의 썸네일 이미지
   */
  @Transactional
  public void editSmallGroupDescriptionWithMultipart(MemberDetails principal, String path,
      UpdateGroupWithMultipartImageRequest descriptionInfo, MultipartFile image) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(principal, smallGroup);

    if (!image.isEmpty()) { // 이미지 값이 있을 때만 저장
      String storedFileName = imageUploadService.uploadImage(image);
      smallGroup.updateImage(storedFileName);
    }

    smallGroup.updateSmallGroupDescription(
        descriptionInfo.getShortDescription(),
        descriptionInfo.getFullDescription());
  }

  /**
   * 소모임 수정에 대한 유효성 체크를 합니다.
   * - 관리자만 해당 소모임의 정보를 수정할 수 있습니다.
   * @param principal 로그인한 회원의 정보
   * @param smallGroup 소모임
   */
  private void validateUpdatable(MemberDetails principal, SmallGroup smallGroup) {
    if (!smallGroup.isManager(principal.getMemberId())) {
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

  /**
   * 배너 이미지를 수정합니다. (base64 이미지)
   * 관리자만 수정 가능합니다.
   * 수정 전 이미지는 삭제됩니다.
   * @param memberDetails
   * @param path
   * @param request
   */
  @Transactional
  public void updateBanner(MemberDetails memberDetails, String path, UpdateBannerRequest request) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(memberDetails, smallGroup);

    // 이미지 업로드
    String storedFileName = imageUploadService.uploadImage(request.getImage(), request.getOriginalImageName());

    replaceBannerImage(smallGroup, storedFileName);
  }

  private void replaceBannerImage(SmallGroup smallGroup, String storedFileName) {
    String originalImage = smallGroup.getBanner();
    smallGroup.updateBanner(storedFileName);

    // 기존 이미지 삭제
    if (StringUtils.hasText(originalImage)) {
      imageUploadService.deleteImage(originalImage);
    }
  }

  /**
   * 배너 이미지를 수정합니다. (MultipartFile 형식 이미지)
   * @param memberDetails
   * @param path
   * @param multipartImage
   */
  @Transactional
  public void updateBanner(MemberDetails memberDetails, String path, MultipartFile multipartImage) {

    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(memberDetails, smallGroup);

    // 이미지 업로드
    String storedFileName = imageUploadService.uploadImage(multipartImage);

    replaceBannerImage(smallGroup, storedFileName);
  }

  @Transactional
  public void toggleUseBanner(MemberDetails memberDetails, String path) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(memberDetails, smallGroup);
    smallGroup.toggleUseBanner();
  }

  public List<String> getInterests(String path) {
    SmallGroup smallGroupByInterests = findWithInterestByPath(path);
    return smallGroupByInterests.getInterests();
  }

  private SmallGroup findWithInterestByPath(String path) {
    return smallGroupRepository.findWithInterestByPath(path)
        .orElseThrow(() -> new IllegalArgumentException(SMALL_GROUP_NOT_FOUND.getDescription()));
  }

  @Transactional
  public void publishSmallGroup(MemberDetails principal, String path) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(principal, smallGroup);
    smallGroup.publish();
  }

  @Transactional
  public void openRecruiting(MemberDetails principal, String path, RecruitingType recruitingType) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(principal, smallGroup);
    smallGroup.openRecruiting(recruitingType);
  }

  @Transactional
  public void closeSmallGroup(MemberDetails principal, String path) {
    SmallGroup smallGroup = findSmallGroupByPath(path);
    validateUpdatable(principal, smallGroup);
    smallGroup.close();
  }
}
