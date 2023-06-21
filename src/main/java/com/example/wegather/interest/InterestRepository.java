package com.example.wegather.interest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
  boolean existsByName(String name);
}
