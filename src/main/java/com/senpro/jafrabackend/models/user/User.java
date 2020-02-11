package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class User {
  @Id private String id;
  private final String name;
  private final String email;
  private String username;
  private List <String> visitedRestaurants;
  private List <Preference> preferences;
}
