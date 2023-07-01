package com.example.wegather.group.domain;

import com.example.wegather.global.vo.Address;
import com.example.wegather.group.domain.repotitory.GroupRepository;
import com.example.wegather.group.dto.CreateGroupRequest;
import com.example.wegather.group.dto.GroupDto;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.example.wegather.group.vo.MaxMemberCount;
import com.example.wegather.member.domain.Member;
import com.example.wegather.member.domain.MemberRepository;
import com.example.wegather.member.domain.vo.Username;
import java.util.List;
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
  private final GroupRepository groupRepository;
  private final MemberRepository memberRepository;

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

  public List<Group> searchGroups(GroupSearchCondition cond) {
    return groupRepository.search(cond);
  }
}
