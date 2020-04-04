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
  private final String email;
  private String password;
  @Id private String username;
  private List<String> hiddenRestaurants; // list of IDs
  private List<CuisinePreference> cuisinePreferences;
  private RatingPreference ratingPreference;
  private PricePreference pricePreference;

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.hiddenRestaurants = new ArrayList<>();
    this.cuisinePreferences = new ArrayList<>();
    this.pricePreference = new PricePreference();
    this.ratingPreference = new RatingPreference();
  }
}
