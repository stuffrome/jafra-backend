package com.senpro.jafrabackend.models.user.preferences;

import lombok.Data;

@Data
public class DistancePreference {
  // stores the average distance that the user enjoys traveling
  // will being under this weight will give a positive boost
  private float preferenceWeigh = 0;
}
