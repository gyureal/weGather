package com.example.wegather.global.vo;

import static com.example.wegather.global.Message.Error.PHONE_NUMBER_RULE_VIOLATION;

import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {
  private static final Pattern PHONE_NUMBER_RULE = Pattern.compile("^01([0|1|6|7|8|9])-?(\\d{3,4})-?(\\d{4})$");
  private String value;

  private PhoneNumber(String value) {
    validatePhoneNumberRule(value);
    this.value = value;
  }

  public static PhoneNumber of(String value) {
    return new PhoneNumber(value);
  }

  void validatePhoneNumberRule(String value) {
    if (!PHONE_NUMBER_RULE.matcher(value).matches()) {
      throw new IllegalArgumentException(PHONE_NUMBER_RULE_VIOLATION);
    }
  }
}
