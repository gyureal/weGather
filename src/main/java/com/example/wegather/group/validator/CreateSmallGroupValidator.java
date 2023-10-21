package com.example.wegather.group.validator;

import com.example.wegather.group.domain.entity.SmallGroup;
import com.example.wegather.group.domain.repotitory.SmallGroupRepository;
import com.example.wegather.group.dto.CreateSmallGroupRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateSmallGroupValidator implements Validator {
  private final SmallGroupRepository smallGroupRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return CreateSmallGroupRequest.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    CreateSmallGroupRequest request = (CreateSmallGroupRequest) target;

    Optional<SmallGroup> smallGroup = smallGroupRepository.findByPath(request.getPath());
    if (smallGroup.isPresent()) {
      errors.rejectValue("path", "wrong.value", "이미 존재하는 url 입니다.");
    }
  }
}
