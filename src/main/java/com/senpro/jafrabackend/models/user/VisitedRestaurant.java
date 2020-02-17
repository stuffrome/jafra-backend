package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Document(collection = "visited")
public class VisitedRestaurant {
  private String username;
  private String restaurantID;
  private float userRating;
  @LastModifiedDate private Date reviewDate;
}
