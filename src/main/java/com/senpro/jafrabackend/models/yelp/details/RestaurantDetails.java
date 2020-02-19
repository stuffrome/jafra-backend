package com.senpro.jafrabackend.models.yelp.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 *  This object contains the same fields the Yelp API Business/Details call contains
 *  https://api.yelp.com/v3/businesses/id
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDetails extends Restaurant {
  private List<String> photos;
  private Location location;
  private List<Hours> hours;
}
