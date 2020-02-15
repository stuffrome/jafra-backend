package com.senpro.jafrabackend.models.yelp;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseWrapper {
  @JsonAlias(value = "businesses")
  private List<Restaurant> restaurants;
}
