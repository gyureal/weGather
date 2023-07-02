package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.Group;
import com.example.wegather.group.dto.GroupSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepositoryQuerydsl {
  Page<Group> search(GroupSearchCondition condition, Pageable pageable);
}
