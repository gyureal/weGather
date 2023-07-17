package com.example.wegather.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableEncryptableProperties
@Configuration
public class JasyptConfig {

  /*
       복호화 키값(jasypt.encryptor.password)는
       Application 실행 시 외부 Environment 를 통해 주입 받는다.

      # JAR 예
      -Djasypt.encryptor.password=jasypt_password.!
     */
  @Value("${jasypt.encryptor.password}")
  private String encryptKey;

  @Bean("jasyptStringEncryptor")
  public StringEncryptor stringEncryptor() {
    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword(encryptKey);
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);

    return encryptor;
  }
}
