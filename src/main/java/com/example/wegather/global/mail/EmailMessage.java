package com.example.wegather.global.mail;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EmailMessage {
  private String to;
  private String subject;
  private String message;
  @Builder.Default
  private Map<String, String> bindingVariables = new HashMap<>();

  public void addBindingVariable(String key, String value) {
    if (bindingVariables.containsKey(key)) {
      throw new IllegalStateException("이미 존재하는 키 입니다.");
    }
    bindingVariables.put(key, value);
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
