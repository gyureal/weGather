package com.example.wegather.group.domain;

import com.example.wegather.auth.MemberDetails;
import com.example.wegather.global.auth.AuthenticationManager;
import com.example.wegather.global.customException.AuthenticationException;
import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.repotitory.GroupRepository;
import com.example.wegather.group.dto.CreateGroupRequest;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.example.wegather.group.dto.UpdateGroupRequest;
import com.example.wegather.group.vo.MaxMemberCount;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {
  private static final String GROUP_NOT_FOUND = "소모임을 찾을 수 없습니다.";
  private static final String USERNAME_IN_AUTH_NOT_FOUND = "인증정보에 있는 회원정보를 찾을 수 없습니다.";
  private static final String DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP = "소모임 정보를 수정할 권한이 없습니다.";
  private static final String DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP = "소모임을 삭제할 권한이 없습니다.";
  private final GroupRepository groupRepository;
  private final MemberRepository memberRepository;
  private final AuthenticationManager authManager;

  @Transactional
  public Group addGroup(CreateGroupRequest request, String username) {

    Member member = memberRepository.findByUsername(Username.of(username))
        .orElseThrow(() -> new IllegalStateException(USERNAME_IN_AUTH_NOT_FOUND));

    return groupRepository.save(Group.builder()
            .name(request.getGroupName())
            .description(request.getDescription())
            .leader(member)
            .address(Address.of(request.getStreetAddress(), request.getLongitude(),
                request.getLatitude()))
            .maxMemberCount(MaxMemberCount.of(request.getMaxMemberCount()))
        .build());
  }

  public Group getGroup(Long id) {
    return groupRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(GROUP_NOT_FOUND));
  }

  public Page<Group> searchGroups(GroupSearchCondition cond, Pageable pageable) {
    return groupRepository.search(cond, pageable);
  }

  @Transactional
  public void editGroup(Long id, UpdateGroupRequest request) {
    Group group = getGroup(id);

    MemberDetails principal = authManager.getPrincipal();

    if (!group.isLeader(principal.getUsername()) && !principal.isAdmin()) {
      throw new AuthenticationException(DO_NOT_HAVE_AUTHORITY_TO_UPDATE_GROUP);
    }
    
    group.updateGroupTotalInfo(
        request.getGroupName(),
        request.getDescription(),
        Address.of(request.getStreetAddress(), request.getLongitude(), request.getLatitude()),
        MaxMemberCount.of(request.getMaxMemberCount()));
  }

  @Transactional
  public void deleteGroup(Long id) {
    Group group = getGroup(id);

    MemberDetails principal = authManager.getPrincipal();

    if (!group.isLeader(principal.getUsername()) && !principal.isAdmin()) {
      throw new AuthenticationException(DO_NOT_HAVE_AUTHORITY_TO_DELETE_GROUP);
    }

    groupRepository.deleteById(id);
  }
}
