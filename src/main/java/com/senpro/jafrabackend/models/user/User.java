package com.senpro.jafrabackend.models.user;

import com.senpro.jafrabackend.models.user.preferences.CuisinePreference;
import com.senpro.jafrabackend.models.user.preferences.PricePreference;
import com.senpro.jafrabackend.models.user.preferences.RatingPreference;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
  private final String name;
  private final String email;
  @Id private String username;
  private List<String> hiddenRestaurants; // list of IDs
  private List<CuisinePreference> cuisinePreferences;
  private RatingPreference ratingPreference;
  private PricePreference pricePreference;
  //private DistancePreference distancePreference;

  public User(String name, String email, String username) {
    this.name = name;
    this.email = email;
    this.username = username;
    this.hiddenRestaurants = new ArrayList<>();
    this.cuisinePreferences = new ArrayList<>();
    this.pricePreference = new PricePreference();
    this.ratingPreference = new RatingPreference();
    //this.distancePreference = new DistancePreference();
  }
}
