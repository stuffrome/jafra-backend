package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.services.RestaurantService;
import com.senpro.jafrabackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

  private RestaurantService restaurantService;
  private UserService userService;

  @Autowired
  public RestaurantController(RestaurantService restaurantService, UserService userService) {
    this.restaurantService = restaurantService;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<Restaurant>> getRestaurants(
      @RequestParam(required = false, defaultValue = "restaurants") String categories,
      @RequestParam String latitude,
      @RequestParam String longitude,
      @RequestParam String radius)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            restaurantService.getRestaurants(
                categories,
                Double.parseDouble(latitude),
                Double.parseDouble(longitude),
                Long.parseLong(radius),
                0));
  }

  // Returns a list of restaurants that contain the name passed in
  @GetMapping("/search")
  public ResponseEntity<List<Restaurant>> findRestaurant(
      @RequestParam String restaurantName, @RequestParam String username)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(restaurantService.findByName(restaurantName, username));
  }

  @GetMapping("/details")
  public ResponseEntity<RestaurantDetails> getRestaurantDetails(@RequestParam String id)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(restaurantService.getRestaurantDetails(id));
  }

  // Updates users recommended restaurants. If latitude and longitude are passed in, the users
  // latitude and longitude will be updated
  @GetMapping("/user")
  public ResponseEntity<String> updateUserRestaurants(
      @RequestParam(required = false, defaultValue = "restaurants") String categories,
      @RequestParam(required = false, defaultValue = "-1") String latitude,
      @RequestParam(required = false, defaultValue = "-1") String longitude,
      @RequestParam String username)
      throws EntityNotFoundException {
    if (latitude.equals("-1") || longitude.equals("-1"))
      restaurantService.updateUserRestaurants(categories, username);
    else {
      userService.updateLatLon(
          username, Double.parseDouble(latitude), Double.parseDouble(longitude));
      restaurantService.updateUserRestaurants(
          categories, username, Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
    return ResponseEntity.ok("Updated");
  }
}
