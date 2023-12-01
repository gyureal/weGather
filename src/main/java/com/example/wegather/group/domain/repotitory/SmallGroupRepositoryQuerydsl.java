package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmallGroupRepositoryQuerydsl {
  Page<SmallGroup> search(String keyword, Pageable pageable);

  Optional<SmallGroup> findWithInterestByPath(String smallGroupPath);
}
