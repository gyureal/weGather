package com.example.wegather.member.domain;

import com.example.wegather.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByUsername(String username);

  Optional<Member> findByUsername(String username);

  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String usernameOrEmail);
}
