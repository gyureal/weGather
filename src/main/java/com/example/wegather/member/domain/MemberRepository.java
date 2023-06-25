package com.example.wegather.member.domain;

import com.example.wegather.member.domain.vo.Username;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByUsername(Username username);
}
