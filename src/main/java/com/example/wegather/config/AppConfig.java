package com.example.wegather.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 테스트 시, LocalDateTime Mocking 을 위해 사용됩니다.
   * @return
   */
  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
