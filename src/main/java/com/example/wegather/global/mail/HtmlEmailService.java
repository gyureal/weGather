package com.example.wegather.global.mail;

import static com.example.wegather.global.exception.ErrorCode.EMAIL_SEND_FAIL;

import com.example.wegather.global.exception.customException.EmailSendFailException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Profile({"dev"})
@Service
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {
  private final JavaMailSender javaMailSender;
  private final ResourceLoader resourceLoader;

  @Override
  public void sendEmail(String templateName, EmailMessage emailMessage) {
    EmailTemplate emailTemplate = loadEmailTemplate(templateName);
    emailMessage.setMessage(emailTemplate.bindVariableToTemplate(emailMessage.getBindingVariables()));

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(emailMessage.getTo());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      javaMailSender.send(mimeMessage);
      log.info("sent email: {}", emailMessage.getMessage());
    } catch (MessagingException e) {
      throw new EmailSendFailException(EMAIL_SEND_FAIL.getDescription(), e);
    }
  }

  private EmailTemplate loadEmailTemplate(String templateName) {
    try {
      String readFile = getResourceAsString(templateName);
      return EmailTemplate.builder()
          .name(templateName)
          .template(readFile)
          .build();
    } catch (IOException e) {
      throw new RuntimeException("템플릿 로드 중 오류가 발생", e);
    }
  }

  private String getResourceAsString(String templateName) throws IOException {
    Resource resource = resourceLoader.getResource("classpath:" + templateName + ".html");
    try (InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }
}
