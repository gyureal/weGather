package com.example.wegather.interest;

import com.example.wegather.global.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Interest extends BaseTimeEntity {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
}
