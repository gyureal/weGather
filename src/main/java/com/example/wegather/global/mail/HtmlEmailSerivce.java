package com.example.wegather.global.mail;

import static com.example.wegather.global.exception.ErrorCode.EMAIL_SEND_FAIL;

import com.example.wegather.global.exception.customException.EmailSendFailException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailSerivce implements EmailService {
  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(EmailMessage emailMessage) {
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
}
