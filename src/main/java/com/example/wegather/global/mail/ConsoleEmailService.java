package com.example.wegather.global.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "test"})
@Component
public class ConsoleEmailService implements EmailService {

  @Override
  public void sendEmail(String templateName, EmailMessage emailMessage) {
    log.info("templateName: {}", templateName);
    log.info("mail to: {}", emailMessage.getTo());
    log.info("mail subject: {}", emailMessage.getSubject());
    log.info("bindingVariables: {}", emailMessage.getBindingVariables());
  }
}
