package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "visited")
public class VisitedRestaurant {
  @Id private VisitedKey id;
  private float userRating;
  private Date reviewDate;
  @Data
  static public class VisitedKey implements Serializable{
      private String username;
      private String restaurantId;
  }
}
