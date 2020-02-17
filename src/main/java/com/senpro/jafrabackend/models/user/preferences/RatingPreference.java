package com.senpro.jafrabackend.models.user.preferences;

import lombok.Data;

@Data
public class RatingPreference {
  // stores a float from 1 to 5, moves towards yelp ratings of positively reviews restaurants
  private float preferenceWeight = 0;
}
