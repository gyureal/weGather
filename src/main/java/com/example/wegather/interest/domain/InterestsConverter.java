package com.example.wegather.interest.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class InterestsConverter implements AttributeConverter<Interests, String> {

  @Override
  public String convertToDatabaseColumn(Interests interests) {
    return interests.convertToString();
  }

  @Override
  public Interests convertToEntityAttribute(String dbData) {
    return Interests.of(dbData);
  }
}
