package com.example.wegather.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * 통합 테스트를 위한 공통 클래스 입니다.
 * 통합 테스트를 구현할 경우 해당 클래스를 확장하여 사용합니다.
 */
@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
  @LocalServerPort
  int port;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
  }
}
