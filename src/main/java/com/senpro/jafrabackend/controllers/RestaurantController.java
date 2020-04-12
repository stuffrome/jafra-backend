package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.RecommendedRestaurantResponse;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin
public class RestaurantController {

  private static final String DEFAULT_CATEGORY = "restaurants";
  private static final String DEFAULT_RADIUS = "40000";

  private RestaurantService restaurantService;

  @Autowired
  public RestaurantController(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  @GetMapping
  public ResponseEntity<List<Restaurant>> getRestaurants(
      @RequestParam(required = false, defaultValue = DEFAULT_CATEGORY) String categories,
      @RequestParam(required = false) String restaurantName,
      @RequestParam String latitude,
      @RequestParam String longitude,
      @RequestParam(required = false, defaultValue = DEFAULT_RADIUS) String radius,
      @RequestParam String username)
      throws EntityNotFoundException {
    System.out.print(SpringVersion.getVersion());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            restaurantService.formatRestaurants(
                restaurantService.getRestaurants(
                    categories,
                    restaurantName,
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude),
                    Long.parseLong(radius),
                    0),
                username));
  }

  @GetMapping("/details")
  public ResponseEntity<RestaurantDetails> getRestaurantDetails(@RequestParam String id)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(restaurantService.getRestaurantDetails(id));
  }

  // Updates users recommended restaurants. If latitude and longitude are passed in, the users
  // latitude and longitude will be updated
  @GetMapping("/recommended")
  public ResponseEntity<RecommendedRestaurantResponse> getRecommendedRestaurants(
      @RequestParam(required = false, defaultValue = DEFAULT_CATEGORY) String categories,
      @RequestParam String latitude,
      @RequestParam String longitude,
      @RequestParam String username,
      @RequestParam String pageNumber,
      @RequestParam String pageSize)
      throws EntityNotFoundException {

    RecommendedRestaurantResponse response =
        restaurantService.getRecommendedRestaurants(
            categories,
            username,
            Double.parseDouble(latitude),
            Double.parseDouble(longitude),
            Integer.parseInt(pageNumber),
            Integer.parseInt(pageSize));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
