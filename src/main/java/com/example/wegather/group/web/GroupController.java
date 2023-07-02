package com.example.wegather.group.web;

import com.example.wegather.group.domain.GroupService;
import com.example.wegather.group.dto.CreateGroupRequest;
import com.example.wegather.group.dto.GroupDto;
import com.example.wegather.group.dto.GroupSearchCondition;
import com.example.wegather.group.dto.UpdateGroupRequest;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/groups")
@RestController
public class GroupController {
  private final GroupService groupService;

  @PostMapping
  public ResponseEntity<GroupDto> createGroup(
      @Valid @RequestBody CreateGroupRequest createGroupRequest,
      Principal principal) {
    GroupDto groupDto = GroupDto.from(groupService.addGroup(createGroupRequest, principal.getName()));
    return ResponseEntity.created(URI.create("/groups/" + groupDto.getId()))
        .body(groupDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GroupDto> readGroup(@PathVariable Long id) {
    return ResponseEntity.ok(GroupDto.from(groupService.getGroup(id)));
  }

  @GetMapping
  public ResponseEntity<Page<GroupDto>> searchGroups(
      @RequestBody GroupSearchCondition cond,
      Pageable pageable) {
    return ResponseEntity.ok(groupService.searchGroups(cond, pageable).map(GroupDto::from));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGroup(
      @PathVariable Long id,
      @RequestBody UpdateGroupRequest request) {

    groupService.editGroup(id, request);
    return ResponseEntity.ok().build();
  }
}
