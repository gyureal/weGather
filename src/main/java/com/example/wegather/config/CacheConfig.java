package com.example.wegather.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableCaching
@Configuration
public class CacheConfig {
  @Bean
  public EhCacheManagerFactoryBean ehCacheCacheManager() {
    EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
    cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
    cmfb.setShared(true);
    return cmfb;
  }
}
