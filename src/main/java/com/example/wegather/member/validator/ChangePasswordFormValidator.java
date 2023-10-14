package com.example.wegather.member.validator;


import com.example.wegather.member.dto.ChangePasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ChangePasswordFormValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return ChangePasswordForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ChangePasswordForm changePasswordForm = (ChangePasswordForm) target;

    if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getNewPasswordCheck())) {
      errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
    }
  }
}
