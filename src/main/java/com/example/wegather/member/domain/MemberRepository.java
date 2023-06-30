package com.example.wegather.member.domain;

import com.example.wegather.member.domain.vo.Username;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByUsername(Username username);

  Optional<Member> findByUsername(Username username);
}
