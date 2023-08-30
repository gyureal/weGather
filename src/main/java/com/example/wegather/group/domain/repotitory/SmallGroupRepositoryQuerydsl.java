package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.dto.SmallGroupSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmallGroupRepositoryQuerydsl {
  Page<SmallGroup> search(SmallGroupSearchCondition condition, Pageable pageable);
}
