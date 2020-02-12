package com.senpro.jafrabackend.models.user.preferences;

import com.senpro.jafrabackend.models.user.Preference;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class PricePreference extends Preference {
  //ranges from 1 to 5, moves towards the price of positively reviewed restaurants
  private float preferenceWeight;
}
