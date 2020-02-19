package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.User;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

  private YelpService apiService;
  private UserService userService;

  @Autowired
  public RestaurantService(YelpService yelpService, UserService userService) {
    this.apiService = yelpService;
    this.userService = userService;
  }

  public void updateUserRestaurants(String username) throws EntityNotFoundException {
    User user = userService.findById(username);
    List<Restaurant> restaurants;
    // restaurants = algorithmService.processRestaurants(getRestaurants(...), user.preferences...);



  }

  public List<Restaurant> getRestaurants(
      String categories, double latitude, double longitude, long radius)
      throws EntityNotFoundException {
    return apiService.getRestaurants(categories, latitude, longitude, radius);
  }

  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {
    return apiService.getRestaurantDetails(id);
  }
}
