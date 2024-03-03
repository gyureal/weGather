package com.example.wegather.global.mail;

public interface EmailService {
  void sendEmail(String templateName, EmailMessage emailMessage);
}
