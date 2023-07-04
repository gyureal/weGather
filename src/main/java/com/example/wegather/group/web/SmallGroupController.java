package com.example.wegather.group.web;

import com.example.wegather.group.domain.SmallGroupService;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import com.example.wegather.group.dto.SmallGroupDto;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import com.example.wegather.group.dto.UpdateSmallGroupRequest;
import java.net.URI;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/smallGroups")
@RestController
public class SmallGroupController {
  private final SmallGroupService smallGroupService;

  @PostMapping
  public ResponseEntity<SmallGroupDto> createGroup(
      @Valid @RequestBody CreateSmallGroupRequest createSmallGroupRequest,
      Principal principal) {
    SmallGroupDto smallGroupDto = SmallGroupDto.from(
        smallGroupService.addGroup(createSmallGroupRequest, principal.getName()));
    return ResponseEntity.created(URI.create("/smallGroups/" + smallGroupDto.getId()))
        .body(smallGroupDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SmallGroupDto> readGroup(@PathVariable Long id) {
    return ResponseEntity.ok(SmallGroupDto.from(smallGroupService.getGroup(id)));
  }

  @GetMapping
  public ResponseEntity<Page<SmallGroupDto>> searchGroups(
      @RequestBody SmallGroupSearchCondition cond,
      Pageable pageable) {
    return ResponseEntity.ok(smallGroupService.searchGroups(cond, pageable).map(SmallGroupDto::from));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGroup(
      @PathVariable Long id,
      @RequestBody UpdateSmallGroupRequest request) {

    smallGroupService.editGroup(id, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
    smallGroupService.deleteGroup(id);
    return ResponseEntity.noContent().build();
  }
}
