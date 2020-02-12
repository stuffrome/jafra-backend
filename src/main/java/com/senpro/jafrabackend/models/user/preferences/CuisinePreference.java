package com.senpro.jafrabackend.models.user.preferences;

import com.senpro.jafrabackend.models.user.Preference;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CuisinePreference extends Preference {
  // cuisine title stores the alias of the cuisine with a weight (ex. chinese, comfortfood, etc)
  @Id private String cuisineTitle;
  //preference weight is a float that increases if the user leaves a positive review on a restaurant in this category
  private float preferenceWeight;
}
