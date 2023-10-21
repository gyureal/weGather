package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupRepository extends JpaRepository<SmallGroup, Long>,
    SmallGroupRepositoryQuerydsl {

  Optional<SmallGroup> findByPath(String path);

}
