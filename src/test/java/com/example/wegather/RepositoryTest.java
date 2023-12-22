package com.example.wegather;

import com.example.wegather.config.TestConfig;
import com.example.wegather.testContainer.MysqlTestContainer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Sql("/truncate.sql")
@DataJpaTest
public abstract class RepositoryTest extends MysqlTestContainer {

}
