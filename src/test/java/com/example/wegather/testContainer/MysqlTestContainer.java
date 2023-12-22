package com.example.wegather.testContainer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ContextConfiguration(initializers = MysqlTestContainer.ContainerPropertiesInitializer.class)
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

  static class ContainerPropertiesInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + mysqlContainer.getJdbcUrl(),
          "spring.datasource.username=" + mysqlContainer.getUsername(),
          "spring.datasource.password=" + mysqlContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
