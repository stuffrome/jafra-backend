package com.senpro.jafrabackend.models.user.preferences;

import lombok.Data;

@Data
public class PricePreference{
  //ranges from 1 to 5, moves towards the price of positively reviewed restaurants
  private float preferenceWeight;
}
