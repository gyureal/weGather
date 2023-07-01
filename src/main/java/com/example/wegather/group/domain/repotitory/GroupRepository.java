package com.example.wegather.group.domain.repotitory;

import com.example.wegather.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryQuerydsl {

}
