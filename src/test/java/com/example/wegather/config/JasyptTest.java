package com.example.wegather.config;

import static org.assertj.core.api.Assertions.*;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JasyptTest {

  @Autowired
  @Qualifier("jasyptStringEncryptor")
  StringEncryptor stringEncryptor;

  @Test
  @DisplayName("비밀번호 암호화 테스트")
  void encryptPassword() {
    String str2 = stringEncryptor.encrypt("pass");

    System.out.println(str2);
    assertThat(str2).isNotBlank();
  }
}
