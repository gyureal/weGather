package com.example.wegather.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class MockIntegrationTest {

}
