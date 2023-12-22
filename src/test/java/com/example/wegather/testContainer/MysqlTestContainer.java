package com.example.wegather.testContainer;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class MysqlTestContainer {
  public static final MySQLContainer<?> mysqlContainer;

  static {
    mysqlContainer = new MySQLContainer<>("mysql:5.7")
        .withDatabaseName("weGather")
        .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci")
        .withReuse(true);

    mysqlContainer.start();
  }

  @DynamicPropertySource
  private static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mysqlContainer::getUsername);
    registry.add("spring.datasource.password", mysqlContainer::getPassword);
  }
}
