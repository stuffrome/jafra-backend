package com.senpro.jafrabackend.models.user.preferences;

import lombok.Data;

@Data
public class CuisinePreference {
  // cuisine title stores the alias of the cuisine with a weight (ex. chinese, comfortfood, etc)
  private String cuisineAlias;
  // preference weight is a float that increases if the user leaves a positive review on a
  // restaurant in this category
  private double preferenceWeight;
}
