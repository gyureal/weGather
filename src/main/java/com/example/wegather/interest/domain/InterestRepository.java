package com.example.wegather.interest.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
  boolean existsByName(String name);
}
