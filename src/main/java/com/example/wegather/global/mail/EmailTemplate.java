package com.example.wegather.global.mail;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor @Builder
@Getter
public class EmailTemplate {
  private String name;
  private String template;


  // TODO: 반복문 돌 때 마다 새로운 String 객체가 생성되므로, 차후 다른 방법을 찾아볼 것
  public String bindVariableToTemplate(Map<String, String> bindingVariables) {
    for (String key: bindingVariables.keySet()) {
      String value = bindingVariables.get(key);
      template = template.replace(String.format("${%s}", key), value);
    }
    return template;
  }

  @Override
  public String toString() {
    return "EmailTemplate{" +
        "name='" + name + '\'' +
        ", template='" + template + '\'' +
        '}';
  }
}
