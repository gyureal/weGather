package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.Group;
import com.example.wegather.group.dto.GroupSearchCondition;
import java.util.List;

public interface GroupRepositoryQuerydsl {
  List<Group> search(GroupSearchCondition condition);
}
