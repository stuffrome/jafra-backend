package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "recommended")
public class RecommendedRestaurantList {
  @Id private String username;
  private List<String> restaurantIds;
}
