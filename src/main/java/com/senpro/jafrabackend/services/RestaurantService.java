package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

  private YelpService apiService;

  @Autowired
  public RestaurantService(YelpService yelpService) {
    this.apiService = yelpService;
  }

  public List<Restaurant> getRestaurants(
      String categories, long latitude, long longitude, long radius)
      throws EntityNotFoundException {
    return apiService.getRestaurants(categories, latitude, longitude, radius);
  }

  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {
    return apiService.getRestaurantDetails(id);
  }
}
