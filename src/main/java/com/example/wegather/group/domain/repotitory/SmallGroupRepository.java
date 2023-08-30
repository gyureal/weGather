package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.entity.SmallGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallGroupRepository extends JpaRepository<SmallGroup, Long>,
    SmallGroupRepositoryQuerydsl {

}
