package com.senpro.jafrabackend.models.user;

import com.senpro.jafrabackend.models.user.preferences.CuisinePreference;
import com.senpro.jafrabackend.models.user.preferences.DistancePreference;
import com.senpro.jafrabackend.models.user.preferences.PricePreference;
import com.senpro.jafrabackend.models.user.preferences.RatingPreference;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class User {
  private String id;
  private final String name;
  private final String email;
  private String username;
  private List <String> hiddenRestaurants;  //list of IDs
  private List <CuisinePreference> cuisinePreferences;
  private List <PricePreference> pricePreferences;
  private RatingPreference ratingPreference;
  private PricePreference pricePreference;
  private DistancePreference distancePreference;
}
