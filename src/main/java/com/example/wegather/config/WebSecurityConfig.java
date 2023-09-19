package com.example.wegather.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    log.info("-------securityFilterChain-------");
    http.csrf().disable();
    http.authorizeRequests()
        .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
        .antMatchers("/", "/view", "/view/sign-in", "/view/sign-up", "/auth/me", "/check-email-token").permitAll()
        .antMatchers(HttpMethod.POST,"/sign-up", "/sign-in", "/check-email-token").permitAll()
        .antMatchers("/health").permitAll()
        .anyRequest().authenticated();

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
